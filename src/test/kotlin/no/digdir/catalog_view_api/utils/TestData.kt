package no.digdir.catalog_view_api.utils

import no.digdir.catalog_view_api.model.*
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

const val MONGO_USER = "testuser"
const val MONGO_PASSWORD = "testpassword"
const val MONGO_PORT = 27017

val MONGO_ENV_VALUES: Map<String, String> = ImmutableMap.of(
    "MONGO_INITDB_ROOT_USERNAME", MONGO_USER,
    "MONGO_INITDB_ROOT_PASSWORD", MONGO_PASSWORD
)

val ELASTIC_ENV_VALUES: Map<String, String> = ImmutableMap.of(
    "cluster.name", "elasticsearch",
    "discovery.type", "single-node",
    "xpack.security.enabled", "true",
    "ELASTIC_PASSWORD","elasticpwd",
    "ES_JAVA_OPTS", "-Xms2G -Xmx2G"
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
    seeAlso = null,
    conceptRelations = null,
    replacedBy = null,
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
    definisjonForAllmennheten = null,
    definisjonForSpesialister = null,
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
    begrepsRelasjon = null,
    assignedUser = null,
    abbreviatedLabel = null,
    interneFelt = null
)

val DB_CONCEPT = EMPTY_INTERNAL_CONCEPT.copy(
    id = "123",
    ansvarligVirksomhet = Virksomhet(id = "111222333"),
    erPublisert = true,
    anbefaltTerm = Term(navn = mapOf("nb" to "title nb", "nn" to "title nn", "en" to "title en")),
    tillattTerm = mapOf("nb" to listOf("nb 0", "nb 1"), "nn" to listOf("nn 0", "nn 1"), "en" to listOf("en 0", "en 1")),
    frarådetTerm = mapOf("nb" to listOf("nb 2", "nb 3"), "nn" to listOf("nn 2", "nn 3"), "en" to listOf("en 2", "en 3")),
    definisjon = Definisjon(
        tekst = mapOf("nb" to "definition nb", "nn" to "definition nn", "en" to "definition en"),
        kildebeskrivelse = Kildebeskrivelse(forholdTilKilde = ForholdTilKildeEnum.EGENDEFINERT)
    ),
    definisjonForAllmennheten = Definisjon(
        tekst = mapOf("nb" to "public nb", "nn" to "public nn", "en" to "public en"),
        kildebeskrivelse = Kildebeskrivelse(
            forholdTilKilde = ForholdTilKildeEnum.SITATFRAKILDE,
            kilde = listOf(URITekst("https://source0", "Source 0"))
        )
    ),
    definisjonForSpesialister = Definisjon(
        tekst = mapOf("nb" to "specialist nb", "nn" to "specialist nn", "en" to "specialist en"),
        kildebeskrivelse = Kildebeskrivelse(
            forholdTilKilde = ForholdTilKildeEnum.BASERTPAAKILDE,
            kilde = listOf(URITekst("https://source1", "Source 1"), URITekst("https://source2", "Source 2"))
        )
    ),
    merknad = mapOf("nb" to "note nb", "nn" to "note nn", "en" to "note en"),
    eksempel = mapOf("nb" to "example nb", "nn" to "example nn", "en" to "example en"),
    omfang = URITekst("https://value-range", "value range"),
    kontaktpunkt = Kontaktpunkt(harEpost = "fellesdatakatalog@digdir.no", harTelefon = "12345678"),
    abbreviatedLabel = "fdk",
    gyldigFom = LocalDate.of(2021, 5, 5),
    gyldigTom = LocalDate.of(2030, 7, 7),
    opprettet = ZonedDateTime.of(2019, 1, 1, 12,0,0,0, ZoneId.of("Europe/Oslo")).toInstant(),
    opprettetAv = "Crea Tor",
    endringslogelement = Endringslogelement(
        endretAv = "Mod Ifier",
        endringstidspunkt = ZonedDateTime.of(2022, 1, 1, 12,0,0,0, ZoneId.of("Europe/Oslo")).toInstant()),
    seOgså = listOf("https://begrep0", "https://begrep1"),
    erstattesAv = listOf("https://begrep1", "https://begrep2"),
    begrepsRelasjon = listOf(
        BegrepsRelasjon(
            relasjon = "assosiativ",
            relasjonsType = "",
            beskrivelse = mapOf("en" to "description"),
            relatertBegrep = "https://begrep0"),
        BegrepsRelasjon(
            relasjon = "assosiativ",
            relasjonsType = "",
            beskrivelse = mapOf("en" to "missing uri"),
            relatertBegrep = null),
        BegrepsRelasjon(
            relasjon = "invalid",
            relasjonsType = "",
            beskrivelse = mapOf("en" to "description"),
            inndelingskriterium = mapOf("en" to "invalid relation type"),
            relatertBegrep = "https://begrep1"),
        BegrepsRelasjon(
            relasjon = "partitiv",
            relasjonsType = "invalid",
            beskrivelse = mapOf("en" to "description"),
            inndelingskriterium = mapOf("en" to "invalid relation subtype"),
            relatertBegrep = "https://begrep2"),
        BegrepsRelasjon(
            relasjon = "generisk",
            relasjonsType = "overordnet",
            beskrivelse = mapOf("en" to "description"),
            inndelingskriterium = mapOf("en" to "generic relation"),
            relatertBegrep = "https://begrep3"),
        BegrepsRelasjon(
            relasjon = "partitiv",
            relasjonsType = "erDelAv",
            beskrivelse = mapOf("en" to "description"),
            inndelingskriterium = mapOf("en" to "partitive relation"),
            relatertBegrep = "https://begrep4")),
    interneFelt = mapOf(
        "text-short-field-id" to InterntFelt(value = "short text"),
        "text-long-field-id" to InterntFelt(value = "long text"),
        "code-list-field-id" to InterntFelt(value = "1"),
        "user-list-field-id" to InterntFelt(value = "1"),
        "boolean-field-id" to InterntFelt(value = "true")
    ),
    fagområdeKoder = listOf("1"),
    assignedUser = "0"
)

val MAPPED_DB_CONCEPT = EMPTY_CONCEPT.copy(
    id = "123",
    publisher = "111222333",
    isPublished = true,
    preferredTerm = LocalizedStrings(nb = "title nb", nn = "title nn", en = "title en"),
    admittedTerm = ListOfLocalizedStrings(nb = listOf("nb 0", "nb 1"), nn = listOf("nn 0", "nn 1"), en = listOf("en 0", "en 1")),
    deprecatedTerm = ListOfLocalizedStrings(nb = listOf("nb 2", "nb 3"), nn = listOf("nn 2", "nn 3"), en = listOf("en 2", "en 3")),
    definition = Definition(
        text=LocalizedStrings(nb="definition nb", nn="definition nn", en="definition en"),
        sourceDescription= SourceDescription(relationshipWithSource="https://data.norge.no/vocabulary/relationship-with-source-type#self-composed", source=emptyList())),
    publicDefinition = Definition(text=LocalizedStrings(nb="public nb", nn="public nn", en="public en"),
        sourceDescription=SourceDescription(
            relationshipWithSource="https://data.norge.no/vocabulary/relationship-with-source-type#direct-from-source",
            source=listOf(URIText(uri="https://source0", text="Source 0")))),
    specialistDefinition=Definition(
        text=LocalizedStrings(nb="specialist nb", nn="specialist nn", en="specialist en"),
        sourceDescription=SourceDescription(
            relationshipWithSource="https://data.norge.no/vocabulary/relationship-with-source-type#derived-from-source",
            source=listOf(URIText(uri="https://source1", text="Source 1"), URIText(uri="https://source2", text="Source 2")))),
    note = LocalizedStrings(nb = "note nb", nn =  "note nn", en = "note en"),
    example = LocalizedStrings(nb = "example nb", nn =  "example nn", en = "example en"),
    valueRange = URIText(uri = "https://value-range", text = "value range"),
    contactPoint = ContactPoint(email = "fellesdatakatalog@digdir.no", telephone = "12345678"),
    abbreviatedLabel = "fdk",
    startDate = LocalDate.of(2021, 5, 5),
    endDate = LocalDate.of(2030, 7, 7),
    created = ZonedDateTime.of(2019, 1, 1, 12,0,0,0, ZoneId.of("Europe/Oslo")).toInstant(),
    createdBy = "Crea Tor",
    lastChanged = ZonedDateTime.of(2022, 1, 1, 12,0,0,0, ZoneId.of("Europe/Oslo")).toInstant(),
    lastChangedBy = "Mod Ifier",
    seeAlso = listOf("https://begrep0", "https://begrep1"),
    replacedBy = listOf("https://begrep1", "https://begrep2"),
    conceptRelations = listOf(
        ConceptRelation(
            relationType="ASSOCIATIVE",
            description=LocalizedStrings(nb=null, nn=null, en="description"),
            relatedConcept="https://begrep0"),
        ConceptRelation(
            relationType="ASSOCIATIVE",
            description=LocalizedStrings(nb=null, nn=null, en="missing uri"),
            relatedConcept=null),
        ConceptRelation(
            relationType=null,
            description=LocalizedStrings(nb=null, nn=null, en="invalid relation type"),
            relatedConcept="https://begrep1"),
        ConceptRelation(
            relationType=null,
            description=LocalizedStrings(nb=null, nn=null, en="invalid relation subtype"),
            relatedConcept="https://begrep2"),
        ConceptRelation(
            relationType="HAS_GENERIC",
            description=LocalizedStrings(nb=null, nn=null, en="generic relation"),
            relatedConcept="https://begrep3"),
        ConceptRelation(
            relationType="HAS_COMPREHENSIVE",
            description=LocalizedStrings(nb=null, nn=null, en="partitive relation"),
            relatedConcept="https://begrep4")),
    internalFields = listOf(
        ShortTextField(
            id="text-short-field-id",
            label=LocalizedStrings(nb="text short nb", nn="text short nn", en="text short en"),
            description=LocalizedStrings(nb="text short desc nb", nn="text short desc nn", en="text short desc en"),
            type="text_short",
            value="short text"),
        LongTextField(
            id="text-long-field-id",
            label=LocalizedStrings(nb="text long nb", nn="text long nn", en="text long en"),
            description=LocalizedStrings(nb="text long desc nb", nn="text long desc nn", en="text long desc en"),
            type="text_long",
            value="long text"),
        CodeField(id="code-list-field-id",
            label=LocalizedStrings(nb="code list nb", nn="code list nn", en="code list en"),
            description=LocalizedStrings(nb="code list desc nb", nn="code list desc nn", en="code list desc en"),
            type="code",
            value=Code(
                codeId=1,
                codeListId="code-list-0",
                codeLabel=LocalizedStrings(nb="Kode 0.1", nn="Kode 0.1", en="Code 0.1"))),
        UserField(
            id="user-list-field-id",
            label=LocalizedStrings(nb="user list nb", nn="user list nn", en="user list en"),
            description=LocalizedStrings(nb="user list desc nb", nn="user list desc nn", en="user list desc en"),
            type="user",
            value=User(
                name="John Doe",
                email="fdk@digdir.no",
                telephone="12345678")),
        BooleanField(
            id="boolean-field-id",
            label=LocalizedStrings(nb="bool nb", nn="bool nn", en="bool en"),
            description=LocalizedStrings(nb="bool desc nb", nn="bool desc nn", en="bool desc en"),
            type="bool",
            value=true)),
    domainCodes = listOf(Code(codeId=1, codeListId="code-list-1", codeLabel=LocalizedStrings(nb="Kode 1.1", nn="Kode 1.1", en="Code 1.1"))),
    assignedUser = User(
        name="Jane Doe",
        email="fdk@digdir.no",
        telephone="87654321")
)

val EMPTY_ADMIN_DATA = CatalogAdminData(
    codeLists = emptyMap(),
    domainCodeList = emptyMap(),
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
    "${CODE_LIST_0.catalogId}-${CODE_LIST_0.id}" to CODE_LIST_0,
    "${CODE_LIST_1.catalogId}-${CODE_LIST_1.id}" to CODE_LIST_1
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
    "${BOOLEAN_FIELD.catalogId}-${BOOLEAN_FIELD.id}" to BOOLEAN_FIELD,
    "${TEXT_SHORT_FIELD.catalogId}-${TEXT_SHORT_FIELD.id}" to TEXT_SHORT_FIELD,
    "${TEXT_LONG_FIELD.catalogId}-${TEXT_LONG_FIELD.id}" to TEXT_LONG_FIELD,
    "${CODE_LIST_FIELD.catalogId}-${CODE_LIST_FIELD.id}" to CODE_LIST_FIELD,
    "${USER_LIST_FIELD.catalogId}-${USER_LIST_FIELD.id}" to USER_LIST_FIELD
)

val DB_INTERNAL_FIELDS = INTERNAL_FIELDS.values.map { it.copy(catalogId = "111222333") }
val DB_CODE_LISTS = CODE_LISTS.values.map { it.copy(catalogId = "111222333") }
val DB_ADMIN_USERS = listOf(
    AdminUser("0", "111222333", "Jane Doe", "fdk@digdir.no", "87654321"),
    AdminUser("1", "111222333", "John Doe", "fdk@digdir.no", "12345678"))
val DB_EDITABLE_FIELDS = listOf(EditableFields("111222333", "code-list-1"))
