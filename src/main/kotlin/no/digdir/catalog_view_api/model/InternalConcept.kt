package no.digdir.catalog_view_api.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.LocalDate

@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class InternalConcept(
    @Id
    val id: String,
    val originaltBegrep: String,
    val versjonsnr: SemVer,
    val revisjonAv: String?,
    val statusURI: String? = null,
    val erPublisert: Boolean = false,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Europe/Oslo")
    val publiseringsTidspunkt: Instant? = null,
    val anbefaltTerm: Term?,
    val tillattTerm: Map<String, List<String>>?,
    val frarådetTerm: Map<String, List<String>>?,
    val definisjon: Definisjon?,
    val folkeligForklaring: Definisjon?,
    val rettsligForklaring: Definisjon?,
    val merknad: Map<String, String>?,
    val ansvarligVirksomhet: Virksomhet,
    val eksempel: Map<String, String>?,
    val fagområde: Map<String, List<String>>?,
    val fagområdeKoder: List<String>?,
    val omfang: URITekst?,
    val kontaktpunkt: Kontaktpunkt?,
    val gyldigFom: LocalDate?,
    val gyldigTom: LocalDate?,
    val endringslogelement: Endringslogelement?,
    val opprettet: Instant? = null,
    val opprettetAv: String? = null,
    val seOgså: List<String>?,
    val erstattesAv: List<String>?,
    val assignedUser: String?,
    val abbreviatedLabel: String?,
    val begrepsRelasjon: List<BegrepsRelasjon>?,
    val interneFelt: Map<String, InterntFelt>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Term (
    val navn: Map<String, String> = HashMap()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Definisjon (
    val tekst: Map<String, String>? = HashMap(),
    val kildebeskrivelse: Kildebeskrivelse?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Virksomhet (
    val id: String,
    val uri: String? = null,
    val navn: String? = null,
    val orgPath: String? = null,
    val prefLabel: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class URITekst (
    val uri: String? = null,
    val tekst: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Kontaktpunkt (
    val harEpost: String? = null,
    val harTelefon: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Endringslogelement(
    val endretAv: String?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Europe/Oslo")
    val endringstidspunkt: Instant
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Kildebeskrivelse (
    val forholdTilKilde: ForholdTilKildeEnum?,
    val kilde: List<URITekst>? = ArrayList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BegrepsRelasjon (
    val relasjon: String? = null,
    val relasjonsType: String? = null,
    val beskrivelse: Map<String, String>? = null,
    val inndelingskriterium: Map<String, String>? = null,
    val relatertBegrep: String? = null
)

enum class ForholdTilKildeEnum(val value: String) {
    EGENDEFINERT("egendefinert"),
    BASERTPAAKILDE("basertPaaKilde"),
    SITATFRAKILDE("sitatFraKilde");

    @JsonValue
    fun jsonValue(): String = value
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class InterntFelt(
    val value: String?
)

data class SemVer(val major: Int, val minor: Int, val patch: Int): Comparable<SemVer> {
    override fun compareTo(other: SemVer): Int =
        compareValuesBy(this, other, { it.major }, { it.minor }, { it.patch })
}
