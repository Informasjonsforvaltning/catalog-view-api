package no.digdir.catalog_view_api.service

import no.digdir.catalog_view_api.elastic.ConceptViewRepository
import no.digdir.catalog_view_api.model.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class ConceptsViewService(private val conceptViewRepository: ConceptViewRepository) {

    fun getConcepts(catalogId: String, changedAfter: Instant?, domainCodes: List<String>?): List<Concept> = when {
        changedAfter != null && domainCodes != null -> conceptViewRepository.getByPublisherAndLastChangedAndDomainCodes(catalogId, changedAfter.toEpochMilli(), domainCodes)
        changedAfter != null -> conceptViewRepository.getByPublisherAndLastChanged(catalogId, changedAfter.toEpochMilli())
        domainCodes != null -> conceptViewRepository.getByPublisherAndDomainCodes(catalogId, domainCodes)
        else -> conceptViewRepository.getByPublisher(catalogId)
    }

    fun getConceptById(catalogId: String, conceptId: String): Concept =
        conceptViewRepository.findByIdOrNull(conceptId)
            ?.takeIf { it.publisher == catalogId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

}
