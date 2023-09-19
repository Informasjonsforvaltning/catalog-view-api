package no.digdir.catalog_view_api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application.databases")
data class MongoDatabases(
    val conceptCatalog: String
)
