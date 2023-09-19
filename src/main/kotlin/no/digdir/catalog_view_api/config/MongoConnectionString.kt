package no.digdir.catalog_view_api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.data.mongodb")
data class MongoConnectionString(
    val uri: String
)
