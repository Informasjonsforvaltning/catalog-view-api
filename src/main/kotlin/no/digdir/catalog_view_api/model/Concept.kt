package no.digdir.catalog_view_api.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(indexName = "catalog-view-concepts")
data class Concept(
    val id: String,
    val idOfOriginalVersion: String,
    val version: SemVer,
    val isPublished: Boolean,
    val publisher: String,
    val status: String?,
    val preferredTerm: LocalizedStrings?,
    val admittedTerm: ListOfLocalizedStrings?,
    val deprecatedTerm: ListOfLocalizedStrings?,
    val definition: Definition?,
    val publicDefinition: Definition?,
    val specialistDefinition: Definition?,
    val note: LocalizedStrings?,
    val valueRange: URIText?,
    val contactPoint: ContactPoint?,
    val abbreviatedLabel: String?,
    val example: LocalizedStrings?,
    val domainCodes: List<Code>?,
    @Field(type = FieldType.Date)
    val startDate: LocalDate?,
    @Field(type = FieldType.Date)
    val endDate: LocalDate?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Europe/Oslo")
    @Field(type = FieldType.Date)
    val created: Instant?,
    val createdBy: String?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Europe/Oslo")
    @Field(type = FieldType.Date)
    val lastChanged: Instant?,
    val lastChangedBy: String?,
    val assignedUser: User?,
    @Field(type = FieldType.Flattened)
    val internalFields: List<FieldInterface>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class LocalizedStrings(
    val nb: String?,
    val nn: String?,
    val en: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ListOfLocalizedStrings(
    val nb: List<String>?,
    val nn: List<String>?,
    val en: List<String>?
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

@JsonInclude(JsonInclude.Include.NON_NULL)
data class User (
    val name: String?,
    val email: String?,
    val telephone: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Code (
    val codeId: Int,
    val codeListId: String,
    val codeLabel: LocalizedStrings
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = ShortTextField::class, name = "text_short"),
    JsonSubTypes.Type(value = LongTextField::class, name = "text_long"),
    JsonSubTypes.Type(value = BooleanField::class, name = "bool"),
    JsonSubTypes.Type(value = UserField::class, name = "user"),
    JsonSubTypes.Type(value = CodeField::class, name = "code")
])
interface FieldInterface {
    val id: String
    val label: LocalizedStrings
    val description: LocalizedStrings
    val type: String
    val value: Any
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ShortTextField(
    override val id: String,
    override val label: LocalizedStrings,
    override val description: LocalizedStrings,
    override val type: String = "text_short",
    override val value: String
): FieldInterface

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LongTextField(
    override val id: String,
    override val label: LocalizedStrings,
    override val description: LocalizedStrings,
    override val type: String = "text_long",
    override val value: String
): FieldInterface

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BooleanField(
    override val id: String,
    override val label: LocalizedStrings,
    override val description: LocalizedStrings,
    override val type: String = "bool",
    override val value: Boolean
): FieldInterface

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserField(
    override val id: String,
    override val label: LocalizedStrings,
    override val description: LocalizedStrings,
    override val type: String = "user",
    override val value: User
): FieldInterface

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CodeField(
    override val id: String,
    override val label: LocalizedStrings,
    override val description: LocalizedStrings,
    override val type: String = "code",
    override val value: Code
): FieldInterface
