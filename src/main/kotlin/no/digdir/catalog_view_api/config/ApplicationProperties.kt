package no.digdir.catalog_view_api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application")
data class ApplicationProperties (
    val scope: String
)
