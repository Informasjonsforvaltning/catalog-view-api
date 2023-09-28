package no.digdir.catalog_view_api.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Concept(
    val id: String,
    val idOfOriginalVersion: String,
    val version: SemVer,
    val publisher: String,
    val status: String?,
    val preferredTerm: LanguageValues?,
    val definition: Definition?,
    val note: LanguageValues?,
    val valueRange: URIText?,
    val contactPoint: ContactPoint?,
    val abbreviatedLabel: String?,
    val example: LanguageValues?,
    val dateValidFrom: LocalDate?,
    val dateValidThrough: LocalDate?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LanguageValues(
    val nb: String?,
    val nn: String?,
    val en: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Definition (
    val text: LanguageValues?,
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
