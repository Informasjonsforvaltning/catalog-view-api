package no.digdir.catalog_view_api.utils

import no.digdir.catalog_view_api.model.AdminCode
import no.digdir.catalog_view_api.model.CatalogAdminData
import no.digdir.catalog_view_api.model.CodeList
import no.digdir.catalog_view_api.model.Concept
import no.digdir.catalog_view_api.model.Field
import no.digdir.catalog_view_api.model.InternalConcept
import no.digdir.catalog_view_api.model.LocalizedStrings
import no.digdir.catalog_view_api.model.SemVer
import no.digdir.catalog_view_api.model.Virksomhet
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap

const val MONGO_USER = "testuser"
const val MONGO_PASSWORD = "testpassword"
const val MONGO_PORT = 27017

val MONGO_ENV_VALUES: Map<String, String> = ImmutableMap.of(
    "MONGO_INITDB_ROOT_USERNAME", MONGO_USER,
    "MONGO_INITDB_ROOT_PASSWORD", MONGO_PASSWORD
)

val EMPTY_CONCEPT = Concept(
    id = "concept-id",
    idOfOriginalVersion = "original-id",
    version = SemVer(1, 0, 0),
    isPublished = false,
    publisher = "123456789",
    status = "http://publications.europa.eu/resource/authority/concept-status/CURRENT",
    preferredTerm = null,
    admittedTerm = null,
    deprecatedTerm = null,
    definition = null,
    publicDefinition = null,
    specialistDefinition = null,
    note = null,
    valueRange = null,
    contactPoint = null,
    abbreviatedLabel = null,
    example = null,
    domainCodes = null,
    startDate = null,
    endDate = null,
    created = null,
    createdBy = null,
    lastChanged = null,
    lastChangedBy = null,
    assignedUser = null,
    internalFields = null
)

val EMPTY_INTERNAL_CONCEPT = InternalConcept(
    id = "concept-id",
    originaltBegrep = "original-id",
    versjonsnr = SemVer(1, 0, 0),
    revisjonAv = null,
    statusURI = "http://publications.europa.eu/resource/authority/concept-status/CURRENT",
    erPublisert = false,
    anbefaltTerm = null,
    tillattTerm = null,
    frarådetTerm = null,
    definisjon = null,
    folkeligForklaring = null,
    rettsligForklaring = null,
    merknad = null,
    ansvarligVirksomhet = Virksomhet(id = "123456789"),
    eksempel = null,
    fagområde = null,
    fagområdeKoder = null,
    omfang = null,
    kontaktpunkt = null,
    gyldigFom = null,
    gyldigTom = null,
    endringslogelement = null,
    opprettet = null,
    opprettetAv = null,
    seOgså = null,
    erstattesAv = null,
    assignedUser = null,
    abbreviatedLabel = null,
    begrepsRelasjon = null,
    interneFelt = null
)

val EMPTY_ADMIN_DATA = CatalogAdminData(
    codeLists = emptyMap(),
    domainCodeList = null,
    internalFields = emptyMap(),
    users = emptyMap()
)

val CODE_0 = AdminCode(0, LocalizedStrings("Kode 0.0", "Kode 0.0", "Code 0.0"), null)
val CODE_1 = AdminCode(1, LocalizedStrings("Kode 0.1", "Kode 0.1", "Code 0.1"), null)
val CODE_2 = AdminCode(0, LocalizedStrings("Kode 1.0", "Kode 1.0", "Code 1.0"), null)
val CODE_3 = AdminCode(1, LocalizedStrings("Kode 1.1", "Kode 1.1", "Code 1.1"), null)

val CODE_LIST_0 = CodeList(id = "code-list-0", catalogId = "123456789", codes = listOf(CODE_0, CODE_1))
val CODE_LIST_1 = CodeList(id = "code-list-1", catalogId = "123456789", codes = listOf(CODE_2, CODE_3))

val CODE_LISTS = mapOf(
    CODE_LIST_0.id to CODE_LIST_0,
    CODE_LIST_1.id to CODE_LIST_1
)

val BOOLEAN_FIELD = Field(
    id = "boolean-field-id",
    label = LocalizedStrings(nb = "bool nb", nn = "bool nn", en = "bool en"),
    description = LocalizedStrings(nb = "bool desc nb", nn = "bool desc nn", en = "bool desc en"),
    catalogId = "123456789",
    type = "boolean",
    codeListId = null
)

val TEXT_SHORT_FIELD = Field(
    id = "text-short-field-id",
    label = LocalizedStrings(nb = "text short nb", nn = "text short nn", en = "text short en"),
    description = LocalizedStrings(nb = "text short desc nb", nn = "text short desc nn", en = "text short desc en"),
    catalogId = "123456789",
    type = "text_short",
    codeListId = null
)

val TEXT_LONG_FIELD = Field(
    id = "text-long-field-id",
    label = LocalizedStrings(nb = "text long nb", nn = "text long nn", en = "text long en"),
    description = LocalizedStrings(nb = "text long desc nb", nn = "text long desc nn", en = "text long desc en"),
    catalogId = "123456789",
    type = "text_long",
    codeListId = null
)

val CODE_LIST_FIELD = Field(
    id = "code-list-field-id",
    label = LocalizedStrings(nb = "code list nb", nn = "code list nn", en = "code list en"),
    description = LocalizedStrings(nb = "code list desc nb", nn = "code list desc nn", en = "code list desc en"),
    catalogId = "123456789",
    type = "code_list",
    codeListId = CODE_LIST_0.id
)

val USER_LIST_FIELD = Field(
    id = "user-list-field-id",
    label = LocalizedStrings(nb = "user list nb", nn = "user list nn", en = "user list en"),
    description = LocalizedStrings(nb = "user list desc nb", nn = "user list desc nn", en = "user list desc en"),
    catalogId = "123456789",
    type = "user_list",
    codeListId = null
)

val INTERNAL_FIELDS = mapOf(
    BOOLEAN_FIELD.id to BOOLEAN_FIELD,
    TEXT_SHORT_FIELD.id to TEXT_SHORT_FIELD,
    TEXT_LONG_FIELD.id to TEXT_LONG_FIELD,
    CODE_LIST_FIELD.id to CODE_LIST_FIELD,
    USER_LIST_FIELD.id to USER_LIST_FIELD
)
