package no.digdir.catalog_view_api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application.postgresql")
data class PostgresProperties(
    val conceptCatalog: DatabaseProperties,
    val adminService: DatabaseProperties
)

data class DatabaseProperties(
    val url: String,
    val username: String,
    val password: String
)
