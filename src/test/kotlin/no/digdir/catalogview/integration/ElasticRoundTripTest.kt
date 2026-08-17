package no.digdir.catalogview.integration

import no.digdir.catalogview.elastic.ConceptViewRepository
import no.digdir.catalogview.elastic.ElasticUpdater
import no.digdir.catalogview.utils.ApiTestContext
import no.digdir.catalogview.utils.MAPPED_DB_CONCEPT
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    properties = ["spring.profiles.active=test"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("integration")
class ElasticRoundTripTest : ApiTestContext() {
    @Autowired
    private lateinit var elasticUpdater: ElasticUpdater

    @Autowired
    private lateinit var conceptViewRepository: ConceptViewRepository

    @Test
    fun `Can read concepts back from index after they have been saved`() {
        elasticUpdater.updateConceptsViewIndex()

        val concepts = conceptViewRepository.getByPublisher(MAPPED_DB_CONCEPT.publisher)

        assertEquals(listOf(MAPPED_DB_CONCEPT), concepts)
    }

    @Test
    fun `Can run index update twice when index already contains documents`() {
        elasticUpdater.updateConceptsViewIndex()
        elasticUpdater.updateConceptsViewIndex()
    }
}
