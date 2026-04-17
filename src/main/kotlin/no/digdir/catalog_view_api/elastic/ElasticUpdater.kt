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

            val allConcepts = try {
                conceptsService.getAndMapAllConcepts()
            } catch (e: Exception) {
                logger.error("Failure in conceptsService.getAndMapAllConcepts when updating elastic index", e)
                throw e
            }

            val viewConcepts = try {
                conceptViewRepository.findAll()
            } catch (e: Exception) {
                logger.error("Failure in conceptViewRepository.findAll when updating elastic index", e)
                throw e
            }

            viewConcepts
                .filter { viewConcept -> allConcepts.none { it.id == viewConcept.id } }
                .run { conceptViewRepository.deleteAll(this) }

            try {
                conceptViewRepository.saveAll(allConcepts)
            } catch (e: Exception) {
                logger.error("Failure in conceptViewRepository.saveAll when updating elastic index", e)
                throw e
            }

            logger.info("finished update of concepts index")
        }
    }

}
