package no.digdir.catalog_view_api.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class AdminUser(
    @Id
    val id: String,
    val catalogId: String,
    val name: String,
    val email: String?,
    val telephoneNumber: String?
)

data class CatalogAdminData(
    val users: Map<String, AdminUser>
)
