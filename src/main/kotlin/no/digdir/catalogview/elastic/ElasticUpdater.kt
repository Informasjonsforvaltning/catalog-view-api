package no.digdir.catalogview.elastic

import no.digdir.catalogview.model.Concept
import no.digdir.catalogview.service.ConceptsService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.searchForStream
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ElasticUpdater(
    private val conceptsService: ConceptsService,
    private val conceptViewRepository: ConceptViewRepository,
    private val elasticsearchOperations: ElasticsearchOperations,
) {
    private val logger = LoggerFactory.getLogger(ElasticUpdater::class.java)

    companion object {
        private const val BATCH_SIZE = 500
        private val INDEX = IndexCoordinates.of("catalog-view-concepts")
    }

    @Scheduled(cron = "0 45 * * * ?")
    fun updateConceptsViewIndex() {
        logger.info("Starting update of concepts index")

        val allConcepts = try {
            conceptsService.getAndMapAllConcepts()
        } catch (e: Exception) {
            logger.error("Failed to fetch concepts from source", e)
            return
        }
        logger.info("Fetched {} concepts from source", allConcepts.size)

        val freshIds = allConcepts.map { it.id }.toSet()

        val staleIds = try {
            getAllIndexedIds() - freshIds
        } catch (e: Exception) {
            logger.error("Failed to read existing IDs from elastic index", e)
            return
        }

        if (staleIds.isNotEmpty()) {
            logger.info("Deleting {} stale documents from index", staleIds.size)
            deleteInBatches(staleIds)
        }

        logger.info("Saving {} concepts to index in batches of {}", allConcepts.size, BATCH_SIZE)
        saveInBatches(allConcepts)

        logger.info("Finished update of concepts index: saved={}, deleted={}", allConcepts.size, staleIds.size)
    }

    private fun getAllIndexedIds(): Set<String> {
        val ids = mutableSetOf<String>()
        val query = NativeQuery.builder()
            .withQuery { q -> q.matchAll { ma -> ma } }
            .withPageable(PageRequest.of(0, BATCH_SIZE))
            .build()

        elasticsearchOperations.searchForStream<Concept>(query, INDEX).use { iterator ->
            iterator.forEach { hit -> hit.id?.let { ids.add(it) } }
        }

        return ids
    }

    private fun deleteInBatches(ids: Set<String>) {
        ids.chunked(BATCH_SIZE).forEach { batch ->
            try {
                conceptViewRepository.deleteAllById(batch)
            } catch (e: Exception) {
                logger.error("Failed to delete batch of {} stale documents", batch.size, e)
            }
        }
    }

    private fun saveInBatches(concepts: List<Concept>) {
        concepts.chunked(BATCH_SIZE).forEachIndexed { index, batch ->
            try {
                conceptViewRepository.saveAll(batch)
                logger.debug("Saved batch {}/{}", index + 1, (concepts.size + BATCH_SIZE - 1) / BATCH_SIZE)
            } catch (e: Exception) {
                logger.error("Failed to save batch of {} concepts", batch.size, e)
            }
        }
    }
}
