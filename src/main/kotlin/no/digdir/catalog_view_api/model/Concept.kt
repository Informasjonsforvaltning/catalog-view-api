package no.digdir.catalog_view_api.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Concept(
    val id: String,
    val idOfOriginalVersion: String,
    val version: SemVer,
    val publisher: String,
    val status: String?,
    val preferredTerm: LocalizedStrings?,
    val definition: Definition?,
    val note: LocalizedStrings?,
    val valueRange: URIText?,
    val contactPoint: ContactPoint?,
    val abbreviatedLabel: String?,
    val example: LocalizedStrings?,
    val dateValidFrom: LocalDate?,
    val dateValidThrough: LocalDate?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Europe/Oslo")
    val created: Instant?,
    val createdBy: String?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Europe/Oslo")
    val lastChanged: Instant?,
    val lastChangedBy: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LocalizedStrings(
    val nb: String?,
    val nn: String?,
    val en: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Definition (
    val text: LocalizedStrings?,
    val sourceDescription: SourceDescription?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SourceDescription (
    val relationshipWithSource: String?,
    val source: List<URIText>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class URIText (
    val uri: String?,
    val text: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContactPoint (
    val email: String?,
    val telephone: String?
)
