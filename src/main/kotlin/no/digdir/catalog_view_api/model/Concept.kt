package no.digdir.catalog_view_api.model

data class Concept(
    val id: String,
    val version: SemVer,
    val publisher: String
)
