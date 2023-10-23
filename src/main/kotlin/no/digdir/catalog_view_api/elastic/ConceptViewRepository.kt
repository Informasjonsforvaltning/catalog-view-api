package no.digdir.catalog_view_api.elastic

import no.digdir.catalog_view_api.model.Concept
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface ConceptViewRepository : ElasticsearchRepository<Concept, String> {
    fun getByPublisher(publisher: String): List<Concept>
}
