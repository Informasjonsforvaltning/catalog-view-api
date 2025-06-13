package no.digdir.catalog_view_api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@JsonIgnoreProperties(ignoreUnknown = true)
data class AdminCode(
    val id: String,
    val name: LocalizedStrings,
    val parentID: String?
)

@Document(collection = "codeLists")
@JsonIgnoreProperties(ignoreUnknown = true)
data class CodeList(
    @Id
    val id: String,
    val catalogId: String,
    val codes: List<AdminCode>
)

@Document(collection = "catalogUsers")
@JsonIgnoreProperties(ignoreUnknown = true)
data class AdminUser(
    @Id
    val id: String,
    val catalogId: String,
    val name: String,
    val email: String?,
    val telephoneNumber: String?
)

@Document(collection = "internalFields")
@JsonIgnoreProperties(ignoreUnknown = true)
data class Field(
    @Id
    val id: String,
    val label: LocalizedStrings,
    val description: LocalizedStrings,
    val catalogId: String,
    val type: String,
    val codeListId: String?
)

@Document(collection = "editableFields")
@JsonIgnoreProperties(ignoreUnknown = true)
data class EditableFields(
    @Id
    val catalogId: String,
    val domainCodeListId: String?
)

data class CatalogAdminData(
    val codeLists: Map<String, CodeList>,
    val domainCodeList: Map<String, String?>,
    val internalFields: Map<String, Field>,
    val users: Map<String, AdminUser>
)
