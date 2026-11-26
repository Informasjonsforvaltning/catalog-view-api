package no.digdir.catalogview.unit

import no.digdir.catalogview.model.InternalConcept
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.jacksonObjectMapper
import kotlin.test.assertEquals

@Tag("unit")
class InternalConceptDeserialization {
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `Null entries in string lists are skipped`() {
        val json =
            """
            {
              "id": "id",
              "originaltBegrep": "id",
              "versjonsnr": { "major": 1, "minor": 0, "patch": 0 },
              "ansvarligVirksomhet": { "id": "123456789" },
              "merkelapp": ["tag", null],
              "fagområdeKoder": [null, "1"],
              "seOgså": [null, "http://example.com/concept"],
              "internSeOgså": [null],
              "erstattesAv": [null, "http://example.com/replacement"],
              "internErstattesAv": ["internal", null]
            }
            """.trimIndent()

        val result = objectMapper.readValue(json, InternalConcept::class.java)

        assertEquals(listOf("tag"), result.merkelapp)
        assertEquals(listOf("1"), result.fagområdeKoder)
        assertEquals(listOf("http://example.com/concept"), result.seOgså)
        assertEquals(emptyList(), result.internSeOgså)
        assertEquals(listOf("http://example.com/replacement"), result.erstattesAv)
        assertEquals(listOf("internal"), result.internErstattesAv)
    }
}
