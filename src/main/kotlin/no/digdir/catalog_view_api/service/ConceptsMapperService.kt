package no.digdir.catalog_view_api.service

import no.digdir.catalog_view_api.config.ApplicationProperties
import no.digdir.catalog_view_api.config.MongoCollections
import no.digdir.catalog_view_api.model.*
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.stereotype.Service

@Service
class ConceptsService(
    private val adminServiceDB: MongoTemplate,
    private val conceptCatalogDB: MongoTemplate,
    private val mongoCollections: MongoCollections,
    private val applicationProperties: ApplicationProperties
) {

    fun getAndMapAllConcepts(): List<Concept> =
        conceptCatalogDB.findAll<InternalConcept>(mongoCollections.concepts)
            .map { it.toExternalDTO(getAllAdminData(), applicationProperties.conceptCatalogBaseURI) }

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
            .associateBy({ "${it.catalogId}-${it.id}" }, { it })

    private fun getDomainCodeListIdFromAdminService(): Map<String, String?> =
        adminServiceDB.findAll<EditableFields>(mongoCollections.editableFields)
            .associateBy({ it.catalogId }, { it.domainCodeListId })

    private fun getUsersFromAdminService(): Map<String, AdminUser> =
        adminServiceDB.findAll<AdminUser>(mongoCollections.users)
            .associateBy({ "${it.catalogId}-${it.id}" }, { it })

}

private fun AdminUser.toDTO(): User =
    User(
        name = name,
        email = email
    )

fun InternalConcept.toExternalDTO(adminData: CatalogAdminData, baseURI: String): Concept {
    val collectionURI = collectionURI(baseURI)
    return Concept(
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
        publicDefinition = definisjonForAllmennheten?.toDefinition(),
        specialistDefinition = definisjonForSpesialister?.toDefinition(),
        note = merknad?.toLangValueObject(),
        attachedTag = merkelapp,
        valueRange = omfang?.toURIText(),
        contactPoint = kontaktpunkt?.let { ContactPoint(email = it.harEpost, telephone = it.harTelefon) },
        abbreviatedLabel = abbreviatedLabel,
        seeAlso = seOgså,
        internalSeeAlso = internSeOgså?.map { conceptURI(it, collectionURI) },
        conceptRelations = begrepsRelasjon?.map { it.toConceptRelation() },
        internalConceptRelations = internBegrepsRelasjon?.map { it.toInternalConceptRelation(collectionURI) },
        replacedBy = erstattesAv,
        internalReplacedBy = internErstattesAv?.map { conceptURI(it, collectionURI) },
        example = eksempel?.toLangValueObject(),
        domain = fagområde?.toLangValueObject(),
        domainCodes = fagområdeKoder?.mapNotNull { adminData.getDomainCode(it, ansvarligVirksomhet.id) }
            ?.ifEmpty { null },
        startDate = gyldigFom,
        endDate = gyldigTom,
        created = opprettet,
        createdBy = opprettetAv,
        lastChanged = endringslogelement?.endringstidspunkt,
        lastChangedBy = endringslogelement?.endretAv,
        assignedUser = assignedUser?.let { adminData.users["${ansvarligVirksomhet.id}-$it"] }?.toDTO(),
        internalFields = interneFelt?.mapNotNull {
            transformInternalField(
                it.key,
                ansvarligVirksomhet.id,
                it.value.value,
                adminData
            )
        }
            ?.ifEmpty { null }
    )
}

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

private fun CatalogAdminData.getDomainCode(id: String?, catalogId: String): Code? {
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
    val fieldType = fieldData?.type?.lowercase()
    return when {
        fieldValue.isNullOrBlank() -> null
        fieldData == null -> null
        fieldType == "boolean" -> fieldData.toBooleanField(fieldValue)
        fieldType == "text_short" -> fieldData.toShortTextField(fieldValue)
        fieldType == "text_long" -> fieldData.toLongTextField(fieldValue)
        fieldType == "code_list" -> fieldData.toCodeField(fieldValue, catalogId, adminData.codeLists)
        fieldType == "user_list" -> fieldData.toUserField(fieldValue, catalogId, adminData.users)
        else -> null
    }
}

private fun Field.toBooleanField(value: String): BooleanField? =
    value.toBooleanStrictOrNull()
        ?.let {
            BooleanField(
                id = id,
                label = label,
                value = it
            )
        }

private fun Field.toShortTextField(value: String): ShortTextField =
    ShortTextField(
        id = id,
        label = label,
        value = value
    )

private fun Field.toLongTextField(value: String): LongTextField =
    LongTextField(
        id = id,
        label = label,
        value = value
    )

private fun Field.toCodeField(value: String, catalogId: String, codeLists: Map<String, CodeList>): CodeField? {
    return if (codeListId == null) null
    else codeLists["$catalogId-$codeListId"]?.codes?.find { it.id == value }
        ?.let {
            CodeField(
                id = id,
                label = label,
                value = Code(
                    codeId = value,
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
            value = it.toDTO()
        )
    }

private enum class RelationType {
    ASSOCIATIVE, HAS_PARTITIVE, HAS_COMPREHENSIVE, HAS_GENERIC, HAS_SPECIFIC
}

private fun String?.toRelationType(subType: String?): RelationType? =
    when {
        this == "assosiativ" -> RelationType.ASSOCIATIVE
        this == "partitiv" && subType == "omfatter" -> RelationType.HAS_PARTITIVE
        this == "partitiv" && subType == "erDelAv" -> RelationType.HAS_COMPREHENSIVE
        this == "generisk" && subType == "underordnet" -> RelationType.HAS_SPECIFIC
        this == "generisk" && subType == "overordnet" -> RelationType.HAS_GENERIC
        else -> null
    }

private fun BegrepsRelasjon.toAssociativeRelation(): ConceptRelation =
    ConceptRelation(
        relationType = RelationType.ASSOCIATIVE.name,
        description = LocalizedStrings(
            nb = beskrivelse?.get("nb"),
            nn = beskrivelse?.get("nn"),
            en = beskrivelse?.get("en")
        ),
        relatedConcept = relatertBegrep
    )

private fun BegrepsRelasjon.toGenericOrPartitiveRelation(relationType: RelationType?): ConceptRelation =
    ConceptRelation(
        relationType = relationType?.name,
        description = LocalizedStrings(
            nb = inndelingskriterium?.get("nb"),
            nn = inndelingskriterium?.get("nn"),
            en = inndelingskriterium?.get("en")
        ),
        relatedConcept = relatertBegrep
    )

private fun BegrepsRelasjon.toConceptRelation(): ConceptRelation {
    val relationType = relasjon.toRelationType(relasjonsType)

    return if (relationType == RelationType.ASSOCIATIVE) toAssociativeRelation()
    else toGenericOrPartitiveRelation(relationType)
}

private fun BegrepsRelasjon.toInternalConceptRelation(conceptBaseURI: String): ConceptRelation =
    toConceptRelation()
        .copy(relatedConcept = relatertBegrep?.let { conceptURI(it, conceptBaseURI) })

private fun InternalConcept.collectionURI(baseURI: String) =
    "$baseURI/collections/${ansvarligVirksomhet.id}"

private fun conceptURI(conceptID: String, collectionURI: String) =
    "$collectionURI/concepts/$conceptID"

