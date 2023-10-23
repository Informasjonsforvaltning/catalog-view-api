package no.digdir.catalog_view_api.elastic

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import no.digdir.catalog_view_api.service.ConceptsService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ElasticUpdater (
    private val conceptsService: ConceptsService,
    private val conceptViewRepository: ConceptViewRepository
){
    private val logger = LoggerFactory.getLogger(ElasticUpdater::class.java)

    @Scheduled(cron = "0 45 * * * ?")
    fun updateConceptsViewIndex() = runBlocking {
        launch {
            logger.info("starting update of concepts index")

            // get updated list of concepts from concept-catalog db
            val allConcepts = conceptsService.getAndMapAllConcepts()
            // find all concepts currently in catalog-view concept-index
            val viewConcepts = conceptViewRepository.findAll()

            // concepts removed from concept-catalog is also removed from view-index
            viewConcepts
                .filter { viewConcept -> allConcepts.none { it.id == viewConcept.id } }
                .run { conceptViewRepository.deleteAll(this) }

            // update all concepts in view-index
            conceptViewRepository.saveAll(allConcepts)

            logger.info("finished update of concepts index")
        }
    }

}
