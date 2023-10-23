package no.digdir.catalog_view_api.unit

import no.digdir.catalog_view_api.model.*
import no.digdir.catalog_view_api.service.toExternalDTO
import no.digdir.catalog_view_api.utils.BOOLEAN_FIELD
import no.digdir.catalog_view_api.utils.CODE_1
import no.digdir.catalog_view_api.utils.CODE_2
import no.digdir.catalog_view_api.utils.CODE_3
import no.digdir.catalog_view_api.utils.CODE_LISTS
import no.digdir.catalog_view_api.utils.CODE_LIST_0
import no.digdir.catalog_view_api.utils.CODE_LIST_1
import no.digdir.catalog_view_api.utils.CODE_LIST_FIELD
import no.digdir.catalog_view_api.utils.EMPTY_ADMIN_DATA
import no.digdir.catalog_view_api.utils.EMPTY_CONCEPT
import no.digdir.catalog_view_api.utils.EMPTY_INTERNAL_CONCEPT
import no.digdir.catalog_view_api.utils.INTERNAL_FIELDS
import no.digdir.catalog_view_api.utils.TEXT_LONG_FIELD
import no.digdir.catalog_view_api.utils.TEXT_SHORT_FIELD
import no.digdir.catalog_view_api.utils.USER_LIST_FIELD
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.assertEquals

@Tag("unit")
class ConceptMapper {

    @Test
    fun `Map id, publisherID, status and version`() {
        val result = EMPTY_INTERNAL_CONCEPT.toExternalDTO(EMPTY_ADMIN_DATA)
        assertEquals(expected = EMPTY_CONCEPT, actual = result)
    }

    @Test
    fun `Map preferred term`() {
        val expected = EMPTY_CONCEPT.copy(
            preferredTerm = LocalizedStrings(
                nb = "bokmål",
                nn = "nynorsk",
                en = "english"
            )
        )

        val result = EMPTY_INTERNAL_CONCEPT.copy(
            anbefaltTerm = Term(navn = mapOf(
                Pair("nb", "bokmål"),
                Pair("nn", "nynorsk"),
                Pair("en", "english")
            ))
        ).toExternalDTO(EMPTY_ADMIN_DATA)

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map definition`() {
        val expected = EMPTY_CONCEPT.copy(
            definition = Definition(
                text = LocalizedStrings(
                    nb = "bokmål",
                    nn = "nynorsk",
                    en = "english"),
                sourceDescription = SourceDescription(
                    relationshipWithSource = "https://data.norge.no/vocabulary/relationship-with-source-type#derived-from-source",
                    source = listOf(
                        URIText(uri = "https://testkilde.no", text = "Testkilde"),
                        URIText(uri = "https://testsource.com", text = "Test source")
                    )
                )
            )
        )

        val result = EMPTY_INTERNAL_CONCEPT.copy(
            definisjon = Definisjon(
                tekst = mapOf(
                    Pair("nb", "bokmål"),
                    Pair("nn", "nynorsk"),
                    Pair("en", "english")),
                kildebeskrivelse = Kildebeskrivelse(
                    forholdTilKilde = ForholdTilKildeEnum.BASERTPAAKILDE,
                    kilde = listOf(
                        URITekst(uri = "https://testkilde.no", tekst = "Testkilde"),
                        URITekst(uri = "https://testsource.com", tekst = "Test source")
                    )
                )
            )
        ).toExternalDTO(EMPTY_ADMIN_DATA)

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map note`() {
        val expected = EMPTY_CONCEPT.copy(
            note = LocalizedStrings(
                nb = "bokmål",
                nn = "nynorsk",
                en = "english"
            )
        )

        val result = EMPTY_INTERNAL_CONCEPT.copy(
            merknad = mapOf(
                Pair("nb", "bokmål"),
                Pair("nn", "nynorsk"),
                Pair("en", "english")
            )
        ).toExternalDTO(EMPTY_ADMIN_DATA)

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map example`() {
        val expected = EMPTY_CONCEPT.copy(
            example = LocalizedStrings(
                nb = "bokmål",
                nn = "nynorsk",
                en = "english"
            )
        )

        val result = EMPTY_INTERNAL_CONCEPT.copy(
            eksempel = mapOf(
                Pair("nb", "bokmål"),
                Pair("nn", "nynorsk"),
                Pair("en", "english")
            )
        ).toExternalDTO(EMPTY_ADMIN_DATA)

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map valid period`() {
        val expected = EMPTY_CONCEPT.copy(
            startDate = LocalDate.of(2021, 7, 11),
            endDate = LocalDate.of(2027, 10, 22)
        )

        val result = EMPTY_INTERNAL_CONCEPT.copy(
            gyldigFom = LocalDate.of(2021, 7, 11),
            gyldigTom = LocalDate.of(2027, 10, 22)
        ).toExternalDTO(EMPTY_ADMIN_DATA)

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map value range`() {
        val expected = EMPTY_CONCEPT.copy(
            valueRange = URIText(uri = "https://omfang.com", text = "Omfang")
        )

        val result = EMPTY_INTERNAL_CONCEPT.copy(
            omfang = URITekst(uri = "https://omfang.com", tekst = "Omfang")
        ).toExternalDTO(EMPTY_ADMIN_DATA)

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map contact point`() {
        val expected = EMPTY_CONCEPT.copy(
            contactPoint = ContactPoint(email = "epost@asdf.no", telephone = "11122233")
        )

        val result = EMPTY_INTERNAL_CONCEPT.copy(
            kontaktpunkt = Kontaktpunkt(harEpost = "epost@asdf.no", harTelefon = "11122233")
        ).toExternalDTO(EMPTY_ADMIN_DATA)

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map created & lastChanged`() {
        val creatDate = ZonedDateTime.of(2019, 1, 1, 12,0,0,0, ZoneId.of("Europe/Oslo")).toInstant()
        val changeDate = ZonedDateTime.of(2022, 1, 1, 12,0,0,0, ZoneId.of("Europe/Oslo")).toInstant()
        val expected = EMPTY_CONCEPT.copy(
            created = creatDate,
            createdBy = "Kari Nordmann",
            lastChanged = changeDate,
            lastChangedBy = "Ola Nordmann"
        )

        val result = EMPTY_INTERNAL_CONCEPT.copy(
            opprettet = creatDate,
            opprettetAv = "Kari Nordmann",
            endringslogelement = Endringslogelement(endretAv = "Ola Nordmann", endringstidspunkt = changeDate)
        ).toExternalDTO(EMPTY_ADMIN_DATA)

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map assigned user`() {
        val adminUser = AdminUser(id = "user-id", catalogId = "123456789", name = "John Doe", email = "epost@asdf.no", telephoneNumber = "11122233")
        val user = User(name = "John Doe", email = "epost@asdf.no", telephone = "11122233")
        val expected = EMPTY_CONCEPT.copy(assignedUser = user)

        val result = EMPTY_INTERNAL_CONCEPT.copy(assignedUser = adminUser.id)
            .toExternalDTO(EMPTY_ADMIN_DATA.copy(users = mapOf("${adminUser.catalogId}-${adminUser.id}" to adminUser)))

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map domain codes`() {
        val expected = EMPTY_CONCEPT.copy(domainCodes = listOf(
            Code(codeId = 0, codeListId = CODE_LIST_1.id, codeLabel = CODE_2.name),
            Code(codeId = 1, codeListId = CODE_LIST_1.id, codeLabel = CODE_3.name)))

        val result = EMPTY_INTERNAL_CONCEPT.copy(fagområdeKoder = listOf("0", "1"))
            .toExternalDTO(EMPTY_ADMIN_DATA.copy(domainCodeList = mapOf("123456789" to CODE_LIST_1.id), codeLists = CODE_LISTS))

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map admitted term`() {
        val expected = EMPTY_CONCEPT.copy(
            admittedTerm = ListOfLocalizedStrings(nb = listOf("bokmål", "tillatt term"), nn = null, en = null)
        )

        val result = EMPTY_INTERNAL_CONCEPT.copy(
            tillattTerm = mapOf(
                Pair("nb", listOf("bokmål", "tillatt term", "")),
                Pair("nn", listOf("")),
                Pair("en", emptyList())
            )
        ).toExternalDTO(EMPTY_ADMIN_DATA)

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map deprecated term`() {
        val expected = EMPTY_CONCEPT.copy(
            deprecatedTerm = ListOfLocalizedStrings(nb = null, nn = null, en = listOf("english"))
        )

        val result = EMPTY_INTERNAL_CONCEPT.copy(
            frarådetTerm = mapOf(
                Pair("nb", emptyList()),
                Pair("en", listOf("english", ""))
            )
        ).toExternalDTO(EMPTY_ADMIN_DATA)

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map public definition`() {
        val expected = EMPTY_CONCEPT.copy(
            publicDefinition = Definition(
                text = LocalizedStrings(
                    nb = null,
                    nn = "nynorsk",
                    en = null),
                sourceDescription = SourceDescription(
                    relationshipWithSource = "https://data.norge.no/vocabulary/relationship-with-source-type#self-composed",
                    source = emptyList()
                )
            )
        )

        val result = EMPTY_INTERNAL_CONCEPT.copy(
            folkeligForklaring = Definisjon(
                tekst = mapOf(
                    Pair("nn", "nynorsk")),
                kildebeskrivelse = Kildebeskrivelse(
                    forholdTilKilde = ForholdTilKildeEnum.EGENDEFINERT
                )
            )
        ).toExternalDTO(EMPTY_ADMIN_DATA)

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map specialist definition`() {
        val expected = EMPTY_CONCEPT.copy(
            specialistDefinition = Definition(
                text = LocalizedStrings(
                    nb = "bokmål",
                    nn = null,
                    en = null),
                sourceDescription = SourceDescription(
                    relationshipWithSource = "https://data.norge.no/vocabulary/relationship-with-source-type#direct-from-source",
                    source = listOf(
                        URIText(uri = "https://testkilde.no", text = "Testkilde"),
                        URIText(uri = "https://testsource.com", text = "Test source")
                    )
                )
            )
        )

        val result = EMPTY_INTERNAL_CONCEPT.copy(
            rettsligForklaring = Definisjon(
                tekst = mapOf(
                    Pair("nb", "bokmål"),
                    Pair("nn", "")),
                kildebeskrivelse = Kildebeskrivelse(
                    forholdTilKilde = ForholdTilKildeEnum.SITATFRAKILDE,
                    kilde = listOf(
                        URITekst(uri = "https://testkilde.no", tekst = "Testkilde"),
                        URITekst(uri = "https://testsource.com", tekst = "Test source")
                    )
                )
            )
        ).toExternalDTO(EMPTY_ADMIN_DATA)

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map boolean internal fields`() {
        val expectedTrue = EMPTY_CONCEPT.copy(internalFields = listOf(
            BooleanField(
                id = BOOLEAN_FIELD.id,
                label = BOOLEAN_FIELD.label,
                description = BOOLEAN_FIELD.description,
                value = true
            )))
        val expectedFalse = EMPTY_CONCEPT.copy(internalFields = listOf(
            BooleanField(
                id = BOOLEAN_FIELD.id,
                label = BOOLEAN_FIELD.label,
                description = BOOLEAN_FIELD.description,
                value = false
            )))

        val resultTrue = EMPTY_INTERNAL_CONCEPT.copy(interneFelt = mapOf(BOOLEAN_FIELD.id to InterntFelt("true")))
            .toExternalDTO(EMPTY_ADMIN_DATA.copy(internalFields = INTERNAL_FIELDS))
        val resultFalse = EMPTY_INTERNAL_CONCEPT.copy(interneFelt = mapOf(BOOLEAN_FIELD.id to InterntFelt("false")))
            .toExternalDTO(EMPTY_ADMIN_DATA.copy(internalFields = INTERNAL_FIELDS))
        val resultInvalid = EMPTY_INTERNAL_CONCEPT.copy(interneFelt = mapOf(BOOLEAN_FIELD.id to InterntFelt("invalid")))
            .toExternalDTO(EMPTY_ADMIN_DATA.copy(internalFields = INTERNAL_FIELDS))

        assertEquals(expected = expectedTrue, actual = resultTrue)
        assertEquals(expected = expectedFalse, actual = resultFalse)
        assertEquals(expected = EMPTY_CONCEPT, actual = resultInvalid)
    }

    @Test
    fun `Map short text internal fields`() {
        val expected = EMPTY_CONCEPT.copy(internalFields = listOf(
            ShortTextField(
                id = TEXT_SHORT_FIELD.id,
                label = TEXT_SHORT_FIELD.label,
                description = TEXT_SHORT_FIELD.description,
                value = "short string value"
            )))

        val result = EMPTY_INTERNAL_CONCEPT.copy(interneFelt = mapOf(TEXT_SHORT_FIELD.id to InterntFelt("short string value")))
            .toExternalDTO(EMPTY_ADMIN_DATA.copy(internalFields = INTERNAL_FIELDS))

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map long text internal fields`() {
        val expected = EMPTY_CONCEPT.copy(internalFields = listOf(
            LongTextField(
                id = TEXT_LONG_FIELD.id,
                label = TEXT_LONG_FIELD.label,
                description = TEXT_LONG_FIELD.description,
                value = "long string value"
            )))

        val result = EMPTY_INTERNAL_CONCEPT.copy(interneFelt = mapOf(TEXT_LONG_FIELD.id to InterntFelt("long string value")))
            .toExternalDTO(EMPTY_ADMIN_DATA.copy(internalFields = INTERNAL_FIELDS))

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map code list internal fields`() {
        val expected = EMPTY_CONCEPT.copy(internalFields = listOf(
            CodeField(
                id = CODE_LIST_FIELD.id,
                label = CODE_LIST_FIELD.label,
                description = CODE_LIST_FIELD.description,
                value = Code(CODE_1.id, CODE_LIST_0.id, CODE_1.name)
            )))

        val result = EMPTY_INTERNAL_CONCEPT.copy(interneFelt = mapOf(CODE_LIST_FIELD.id to InterntFelt("${CODE_1.id}")))
            .toExternalDTO(EMPTY_ADMIN_DATA.copy(internalFields = INTERNAL_FIELDS, codeLists = CODE_LISTS))

        val resultInvalid = EMPTY_INTERNAL_CONCEPT.copy(interneFelt = mapOf(CODE_LIST_FIELD.id to InterntFelt("asdf")))
            .toExternalDTO(EMPTY_ADMIN_DATA.copy(internalFields = INTERNAL_FIELDS, codeLists = CODE_LISTS))

        assertEquals(expected = expected, actual = result)
        assertEquals(expected = EMPTY_CONCEPT, actual = resultInvalid)
    }

    @Test
    fun `Map user list internal fields`() {
        val adminUser = AdminUser(id = "user-id", catalogId = "123456789", name = "John Doe", email = "epost@asdf.no", telephoneNumber = "11122233")
        val expected = EMPTY_CONCEPT.copy(internalFields = listOf(
            UserField(
                id = USER_LIST_FIELD.id,
                label = USER_LIST_FIELD.label,
                description = USER_LIST_FIELD.description,
                value = User(name = "John Doe", email = "epost@asdf.no", telephone = "11122233")
            )))

        val result = EMPTY_INTERNAL_CONCEPT.copy(interneFelt = mapOf(USER_LIST_FIELD.id to InterntFelt(adminUser.id)))
            .toExternalDTO(EMPTY_ADMIN_DATA.copy(internalFields = INTERNAL_FIELDS, users = mapOf("${adminUser.catalogId}-${adminUser.id}" to adminUser)))

        val resultInvalid = EMPTY_INTERNAL_CONCEPT.copy(interneFelt = mapOf(CODE_LIST_FIELD.id to InterntFelt("asdf")))
            .toExternalDTO(EMPTY_ADMIN_DATA.copy(internalFields = INTERNAL_FIELDS, users = mapOf("${adminUser.catalogId}-${adminUser.id}" to adminUser)))

        assertEquals(expected = expected, actual = result)
        assertEquals(expected = EMPTY_CONCEPT, actual = resultInvalid)
    }

}
