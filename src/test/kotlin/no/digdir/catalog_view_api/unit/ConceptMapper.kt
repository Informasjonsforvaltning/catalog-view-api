package no.digdir.catalog_view_api.unit

import no.digdir.catalog_view_api.model.Definisjon
import no.digdir.catalog_view_api.model.Definition
import no.digdir.catalog_view_api.model.ForholdTilKildeEnum
import no.digdir.catalog_view_api.model.Kildebeskrivelse
import no.digdir.catalog_view_api.model.LanguageValues
import no.digdir.catalog_view_api.model.SourceDescription
import no.digdir.catalog_view_api.model.Term
import no.digdir.catalog_view_api.model.URITekst
import no.digdir.catalog_view_api.model.URIText
import no.digdir.catalog_view_api.service.toExternalDTO
import no.digdir.catalog_view_api.utils.EMPTY_CONCEPT
import no.digdir.catalog_view_api.utils.EMPTY_INTERNAL_CONCEPT
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

@Tag("unit")
class ConceptMapper {

    @Test
    fun `Map id, publisherID, status and version`() {
        val result = EMPTY_INTERNAL_CONCEPT.toExternalDTO()
        assertEquals(expected = EMPTY_CONCEPT, actual = result)
    }

    @Test
    fun `Map preferred term`() {
        val expected = EMPTY_CONCEPT.copy(
            preferredTerm = LanguageValues(
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
        ).toExternalDTO()

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map definition`() {
        val expected = EMPTY_CONCEPT.copy(
            definition = Definition(
                text = LanguageValues(
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
        ).toExternalDTO()

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map note`() {
        val expected = EMPTY_CONCEPT.copy(
            note = LanguageValues(
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
        ).toExternalDTO()

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map example`() {
        val expected = EMPTY_CONCEPT.copy(
            example = LanguageValues(
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
        ).toExternalDTO()

        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `Map valid period`() {
        val expected = EMPTY_CONCEPT.copy(
            dateValidFrom = LocalDate.of(2021, 7, 11),
            dateValidThrough = LocalDate.of(2027, 10, 22)
        )

        val result = EMPTY_INTERNAL_CONCEPT.copy(
            gyldigFom = LocalDate.of(2021, 7, 11),
            gyldigTom = LocalDate.of(2027, 10, 22)
        ).toExternalDTO()

        assertEquals(expected = expected, actual = result)
    }

}
