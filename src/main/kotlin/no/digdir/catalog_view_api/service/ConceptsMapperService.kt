package no.digdir.catalog_view_api.service

import no.digdir.catalog_view_api.config.MongoCollections
import no.digdir.catalog_view_api.model.*
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.stereotype.Service

@Service
class ConceptsService(
    private val adminServiceDB: MongoTemplate,
    private val conceptCatalogDB: MongoTemplate,
    private val mongoCollections: MongoCollections
) {

    fun getAndMapAllConcepts(): List<Concept> =
        conceptCatalogDB.findAll<InternalConcept>(mongoCollections.concepts)
            .map { it.toExternalDTO(getAllAdminData()) }

    private fun getAllAdminData(): CatalogAdminData =
        CatalogAdminData(
            codeLists = getCodeListsFromAdminService(),
            domainCodeList = getDomainCodeListIdFromAdminService(),
            internalFields = getInternalFieldsFromAdminService(),
            users = getUsersFromAdminService()
        )

    private fun getCodeListsFromAdminService(): Map<String, CodeList> =
        adminServiceDB.findAll<CodeList>(mongoCollections.codeLists)
            .associateBy({ "${it.catalogId}-${it.id}" }, { it })

    private fun getInternalFieldsFromAdminService(): Map<String, Field> =
        adminServiceDB.findAll<Field>(mongoCollections.internalFields)
            .associateBy({ it.id }, { it })

    private fun getDomainCodeListIdFromAdminService(): Map<String, String?> =
        adminServiceDB.findAll<EditableFields>(mongoCollections.editableFields)
            .associateBy({ it.catalogId }, { it.domainCodeListId })

    private fun getUsersFromAdminService(): Map<String, AdminUser> =
        adminServiceDB.findAll<AdminUser>(mongoCollections.users)
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
        domainCodes = fagområdeKoder?.mapNotNull { adminData.getDomainCode(it.safeToInt(), ansvarligVirksomhet.id) }
            ?.ifEmpty { null },
        startDate = gyldigFom,
        endDate = gyldigTom,
        created = opprettet,
        createdBy = opprettetAv,
        lastChanged = endringslogelement?.endringstidspunkt,
        lastChangedBy = endringslogelement?.endretAv,
        assignedUser = assignedUser?.let { adminData.users["${ansvarligVirksomhet.id}-$it"] }?.toDTO(),
        internalFields = interneFelt?.mapNotNull { transformInternalField(it.key, ansvarligVirksomhet.id, it.value.value, adminData) }
            ?.ifEmpty { null }
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

private fun CatalogAdminData.getDomainCode(id: Int?, catalogId: String): Code? {
    val codeListId = domainCodeList[catalogId]

    return if (id == null || codeListId == null) null
    else codeLists["$catalogId-$codeListId"]?.codes
        ?.find { it.id == id }
        ?.let { Code(codeId = id, codeListId = codeListId, codeLabel = it.name) }
}

private fun String.safeToInt(): Int? =
    try { toInt() } catch (_: Exception) { null }

private fun transformInternalField(fieldId: String, catalogId: String, fieldValue: String?, adminData: CatalogAdminData): FieldInterface? {
    val fieldData = adminData.internalFields["$catalogId-$fieldId"]
    return when {
        fieldValue.isNullOrBlank() -> null
        fieldData == null -> null
        fieldData.type == "boolean" -> fieldData.toBooleanField(fieldValue)
        fieldData.type == "text_short" -> fieldData.toShortTextField(fieldValue)
        fieldData.type == "text_long" -> fieldData.toLongTextField(fieldValue)
        fieldData.type == "code_list" -> fieldData.toCodeField(fieldValue, catalogId, adminData.codeLists)
        fieldData.type == "user_list" -> fieldData.toUserField(fieldValue, catalogId, adminData.users)
        else -> null
    }
}

private fun Field.toBooleanField(value: String): BooleanField? =
    value.toBooleanStrictOrNull()
        ?.let {
            BooleanField(
                id = id,
                label = label,
                description = description,
                value = it
            )
        }

private fun Field.toShortTextField(value: String): ShortTextField =
    ShortTextField(
        id = id,
        label = label,
        description = description,
        value = value
    )

private fun Field.toLongTextField(value: String): LongTextField =
    LongTextField(
        id = id,
        label = label,
        description = description,
        value = value
    )

private fun Field.toCodeField(value: String, catalogId: String, codeLists: Map<String, CodeList>): CodeField? {
    val codeId = value.safeToInt()
    return if (codeId == null || codeListId == null) null
    else codeLists["$catalogId-$codeListId"]?.codes?.find { it.id == codeId }
        ?.let {
            CodeField(
                id = id,
                label = label,
                description = description,
                value = Code(
                    codeId = codeId,
                    codeListId = codeListId,
                    codeLabel = it.name
                )
            )
        }
}

private fun Field.toUserField(value: String, catalogId: String, users: Map<String, AdminUser>): UserField? =
    users["$catalogId-$value"]?.let {
        UserField(
            id = id,
            label = label,
            description = description,
            value = it.toDTO()
        )
    }
