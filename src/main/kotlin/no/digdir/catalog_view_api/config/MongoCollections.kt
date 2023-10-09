package no.digdir.catalog_view_api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application.collections")
data class MongoCollections(
    val concepts: String,
    val users: String
)
