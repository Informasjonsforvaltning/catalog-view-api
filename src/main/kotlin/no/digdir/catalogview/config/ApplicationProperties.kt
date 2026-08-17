package no.digdir.catalogview.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application")
data class ApplicationProperties(val scope: String, val conceptCatalogBaseURI: String)
