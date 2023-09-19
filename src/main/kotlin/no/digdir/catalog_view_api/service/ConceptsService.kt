package no.digdir.catalog_view_api.service

import no.digdir.catalog_view_api.model.Concept
import no.digdir.catalog_view_api.model.InternalConcept
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ConceptsService(
    private val conceptCatalogDB: MongoTemplate
) {
    private val conceptMongoCollection = "begrep"

    fun getConcepts(catalogId: String): List<Concept> =
        Criteria.where("ansvarligVirksomhet.id").`is`(catalogId)
            .let { Query(it) }
            .let { query -> conceptCatalogDB.find<InternalConcept>(query, conceptMongoCollection) }
            .map { it.toExternalDTO() }

    fun getConceptById(catalogId: String, conceptId: String): Concept =
        conceptCatalogDB.findById<InternalConcept>(conceptId, conceptMongoCollection)
            ?.also { if (it.ansvarligVirksomhet?.id != catalogId) throw ResponseStatusException(HttpStatus.NOT_FOUND) }
            ?.toExternalDTO()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    fun InternalConcept.toExternalDTO(): Concept =
        Concept(
            id = id,
            version = versjonsnr,
            publisher = ansvarligVirksomhet.id
        )

}
