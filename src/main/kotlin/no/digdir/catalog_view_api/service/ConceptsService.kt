package no.digdir.catalog_view_api.service

import no.digdir.catalog_view_api.config.MongoCollections
import no.digdir.catalog_view_api.model.*
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ConceptsService(
    private val adminServiceDB: MongoTemplate,
    private val conceptCatalogDB: MongoTemplate,
    private val mongoCollections: MongoCollections
) {

    fun getConcepts(catalogId: String): List<Concept> =
        Criteria.where("ansvarligVirksomhet.id").`is`(catalogId)
            .let { Query(it) }
            .let { query -> conceptCatalogDB.find<InternalConcept>(query, mongoCollections.concepts) }
            .map { it.toExternalDTO(getAdminData(catalogId)) }

    fun getConceptById(catalogId: String, conceptId: String): Concept =
        conceptCatalogDB.findById<InternalConcept>(conceptId, mongoCollections.concepts)
            ?.also { if (it.ansvarligVirksomhet.id != catalogId) throw ResponseStatusException(HttpStatus.NOT_FOUND) }
            ?.toExternalDTO(getAdminData(catalogId))
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    private fun getAdminData(catalogId: String): CatalogAdminData =
        CatalogAdminData(
            codeLists = getCodeListsFromAdminService(catalogId),
            domainCodeList = getDomainCodeListIdFromAdminService(catalogId),
            users = getUsersFromAdminService(catalogId)
        )

    private fun getCodeListsFromAdminService(catalogId: String): Map<String, CodeList> =
        Criteria.where("catalogId").`is`(catalogId)
            .let { Query(it) }
            .let { query -> adminServiceDB.find<CodeList>(query, mongoCollections.codeLists) }
            .associateBy({ it.id }, { it })

    private fun getDomainCodeListIdFromAdminService(catalogId: String): String? =
        adminServiceDB.findById<EditableFields>(catalogId, mongoCollections.editableFields)
            ?.domainCodeListId

    private fun getUsersFromAdminService(catalogId: String): Map<String, AdminUser> =
        Criteria.where("catalogId").`is`(catalogId)
            .let { Query(it) }
            .let { query -> adminServiceDB.find<AdminUser>(query, mongoCollections.users) }
            .associateBy({ it.id }, { it })

}

private fun AdminUser.toDTO(): User =
    User(
        name = name,
        email = email,
        telephone = telephoneNumber
    )

fun InternalConcept.toExternalDTO(adminData: CatalogAdminData): Concept =
    Concept(
        id = id,
        idOfOriginalVersion = originaltBegrep,
        version = versjonsnr,
        isPublished = erPublisert,
        publisher = ansvarligVirksomhet.id,
        status = statusURI,
        preferredTerm = anbefaltTerm?.navn?.toLangValueObject(),
        admittedTerm = tillattTerm?.toLangValueObject(),
        deprecatedTerm = frarådetTerm?.toLangValueObject(),
        definition = definisjon?.toDefinition(),
        publicDefinition = folkeligForklaring?.toDefinition(),
        specialistDefinition = rettsligForklaring?.toDefinition(),
        note = merknad?.toLangValueObject(),
        valueRange = omfang?.toURIText(),
        contactPoint = kontaktpunkt?.let { ContactPoint(email = it.harEpost, telephone = it.harTelefon) },
        abbreviatedLabel = abbreviatedLabel,
        example = eksempel?.toLangValueObject(),
        domainCodes = fagområdeKoder?.mapNotNull { adminData.getDomainCode(it.safeToInt()) }
            ?.ifEmpty { null },
        startDate = gyldigFom,
        endDate = gyldigTom,
        created = opprettet,
        createdBy = opprettetAv,
        lastChanged = endringslogelement?.endringstidspunkt,
        lastChangedBy = endringslogelement?.endretAv,
        assignedUser = assignedUser?.let { adminData.users[it] }?.toDTO()
    )

private fun Map<String, String>.toLangValueObject(): LocalizedStrings? {
    val langValues = LocalizedStrings(
        nb = get("nb")?.ifBlank { null },
        nn = get("nn")?.ifBlank { null },
        en = get("en")?.ifBlank { null }
    )

    return when {
        langValues.nb != null -> langValues
        langValues.nn != null -> langValues
        langValues.en != null -> langValues
        else -> null
    }
}

private fun Map<String, List<String>>.toLangValueObject(): ListOfLocalizedStrings? {
    val langValues = ListOfLocalizedStrings(
        nb = get("nb")?.mapNotNull { it.ifBlank { null } }?.ifEmpty { null },
        nn = get("nn")?.mapNotNull { it.ifBlank { null } }?.ifEmpty { null },
        en = get("en")?.mapNotNull { it.ifBlank { null } }?.ifEmpty { null }
    )

    return when {
        langValues.nb != null -> langValues
        langValues.nn != null -> langValues
        langValues.en != null -> langValues
        else -> null
    }
}

private fun Definisjon.toDefinition(): Definition {
    return Definition(
        text = tekst?.toLangValueObject(),
        sourceDescription = SourceDescription(
            relationshipWithSource = kildebeskrivelse?.forholdTilKilde?.toURI(),
            source = kildebeskrivelse?.kilde?.map { it.toURIText() }
        )
    )
}

private fun ForholdTilKildeEnum.toURI(): String =
    when (this) {
        ForholdTilKildeEnum.EGENDEFINERT -> "https://data.norge.no/vocabulary/relationship-with-source-type#self-composed"
        ForholdTilKildeEnum.BASERTPAAKILDE -> "https://data.norge.no/vocabulary/relationship-with-source-type#derived-from-source"
        ForholdTilKildeEnum.SITATFRAKILDE -> "https://data.norge.no/vocabulary/relationship-with-source-type#direct-from-source"
    }

private fun URITekst.toURIText(): URIText =
    URIText(uri = uri, text = tekst)

private fun CatalogAdminData.getDomainCode(id: Int?): Code? =
    if (id == null || domainCodeList == null) null
    else codeLists[domainCodeList]?.codes
        ?.find { it.id == id }
        ?.let { Code(codeId = id, codeListId = domainCodeList, codeLabel = it.name) }

private fun String.safeToInt(): Int? =
    try { toInt() } catch (_: Exception) { null }
