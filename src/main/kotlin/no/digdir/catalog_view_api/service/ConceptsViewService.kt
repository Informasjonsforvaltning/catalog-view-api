package no.digdir.catalog_view_api.service

import no.digdir.catalog_view_api.elastic.ConceptViewRepository
import no.digdir.catalog_view_api.model.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ConceptsViewService(private val conceptViewRepository: ConceptViewRepository) {

    fun getConcepts(catalogId: String): List<Concept> =
        conceptViewRepository.getByPublisher(catalogId)

    fun getConceptById(catalogId: String, conceptId: String): Concept =
        conceptViewRepository.findByIdOrNull(conceptId)
            ?.takeIf { it.publisher == catalogId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

}
