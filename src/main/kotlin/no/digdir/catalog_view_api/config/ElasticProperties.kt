package no.digdir.catalog_view_api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application.elastic")
data class ElasticProperties (
    val username: String,
    val password: String,
    val host: String,
    val ssl: Boolean,
    val storeName: String,
    val storePass: String,
    val certPath: String
)
