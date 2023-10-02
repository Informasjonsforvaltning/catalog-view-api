package no.digdir.catalog_view_api.service

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
    private val conceptCatalogDB: MongoTemplate
) {
    private val conceptMongoCollection = "begrep"

    fun getConcepts(catalogId: String): List<Concept> =
        Criteria.where("ansvarligVirksomhet.id").`is`(catalogId)
            .let { Query(it) }
            .let { query -> conceptCatalogDB.find<InternalConcept>(query, conceptMongoCollection) }
            .map { it.toExternalDTO() }

    fun getConceptById(catalogId: String, conceptId: String): Concept =
        conceptCatalogDB.findById<InternalConcept>(conceptId, conceptMongoCollection)
            ?.also { if (it.ansvarligVirksomhet.id != catalogId) throw ResponseStatusException(HttpStatus.NOT_FOUND) }
            ?.toExternalDTO()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

}

fun InternalConcept.toExternalDTO(): Concept =
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
        startDate = gyldigFom,
        endDate = gyldigTom,
        created = opprettet,
        createdBy = opprettetAv,
        lastChanged = endringslogelement?.endringstidspunkt,
        lastChangedBy = endringslogelement?.endretAv
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
