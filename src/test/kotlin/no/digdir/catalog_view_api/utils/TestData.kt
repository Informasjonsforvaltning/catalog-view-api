package no.digdir.catalog_view_api.utils

import no.digdir.catalog_view_api.model.AdminCode
import no.digdir.catalog_view_api.model.CatalogAdminData
import no.digdir.catalog_view_api.model.CodeList
import no.digdir.catalog_view_api.model.Concept
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
    assignedUser = null
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
