package no.digdir.catalogview.elastic

import no.digdir.catalogview.model.Concept
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface ConceptViewRepository : ElasticsearchRepository<Concept, String> {
    @Query("{\"bool\" : {\"filter\" : [{\"term\" : {\"publisher.keyword\" : \"?0\"}}]}}")
    fun getByPublisher(publisher: String): List<Concept>

    @Query(
        "{\"bool\" : {\"filter\" : [{\"term\" : {\"publisher.keyword\" : \"?0\"}}, {\"range\" : {\"lastChanged\": {\"gte\": \"?1\"}}}]}}",
    )
    fun getByPublisherAndLastChanged(publisher: String, changedAfter: Long): List<Concept>

    @Query(
        "{\"bool\" : {\"filter\" : [{\"term\" : {\"publisher.keyword\" : \"?0\"}}, {\"terms\" : {\"domainCodes.codeId.keyword\": ?1}}]}}",
    )
    fun getByPublisherAndDomainCodes(publisher: String, domainCodes: List<String>): List<Concept>

    @Query(
        "{\"bool\" : {\"filter\" : [{\"term\" : {\"publisher.keyword\" : \"?0\"}}, {\"range\" : {\"lastChanged\": {\"gte\": \"?1\"}}}, {\"terms\" : {\"domainCodes.codeId.keyword\": ?2}}]}}",
    )
    fun getByPublisherAndLastChangedAndDomainCodes(publisher: String, changedAfter: Long, domainCodes: List<String>): List<Concept>
}
