package no.digdir.catalog_view_api.service

import no.digdir.catalog_view_api.model.Concept
import org.springframework.stereotype.Service

@Service
class ConceptsService {

    fun getConcepts(catalogId: String): List<Concept> = emptyList()

    fun getConceptById(catalogId: String, conceptId: String): Concept? = null

}
