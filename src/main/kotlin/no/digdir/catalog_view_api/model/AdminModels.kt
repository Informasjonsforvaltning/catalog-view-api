package no.digdir.catalog_view_api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AdminCode(
    val id: String,
    val name: LocalizedStrings,
    val parentID: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CodeList(
    val id: String,
    val catalogId: String,
    val codes: List<AdminCode>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AdminUser(
    val id: String,
    val catalogId: String,
    val name: String,
    val email: String?,
    val telephoneNumber: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Field(
    val id: String,
    val label: LocalizedStrings,
    val description: LocalizedStrings,
    val catalogId: String,
    val type: String,
    val codeListId: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EditableFields(
    val catalogId: String,
    val domainCodeListId: String?
)

data class CatalogAdminData(
    val codeLists: Map<String, CodeList>,
    val domainCodeList: Map<String, String?>,
    val internalFields: Map<String, Field>,
    val users: Map<String, AdminUser>
)
