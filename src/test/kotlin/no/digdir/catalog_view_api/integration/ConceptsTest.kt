package no.digdir.catalog_view_api.integration

import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.catalog_view_api.config.JacksonConfigurer
import no.digdir.catalog_view_api.elastic.ElasticUpdater
import no.digdir.catalog_view_api.model.Concept
import no.digdir.catalog_view_api.utils.ApiTestContext
import no.digdir.catalog_view_api.utils.MAPPED_DB_CONCEPT
import no.digdir.catalog_view_api.utils.apiAuthorizedRequest
import no.digdir.catalog_view_api.utils.jwk.JwtToken
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    properties = ["spring.profiles.active=test"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("integration")
class ConceptsTest: ApiTestContext() {
    private val mapper = JacksonConfigurer().objectMapper()

    @Autowired
    private lateinit var elasticUpdater: ElasticUpdater

    @BeforeAll
    fun populateElastic() {
        elasticUpdater.updateConceptsViewIndex()
    }

    @Nested
    internal inner class GetConceptById {
        private val path = "/catalogs/111222333/concepts/123"

        @Test
        fun `Responds with Unauthorized when missing token`() {
            val response = apiAuthorizedRequest(path, port, null, null, HttpMethod.GET)
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response["status"])
        }

        @Test
        fun `Responds with Forbidden when authorized for wrong scope`() {
            val response = apiAuthorizedRequest(path, port, null, JwtToken("111222333", "datanorge:test/wrong.scope").toString(), HttpMethod.GET)
            Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response["status"])
        }

        @Test
        fun `Responds with Forbidden when authorized for wrong catalog`() {
            val response = apiAuthorizedRequest(path, port, null, JwtToken("333222111").toString(), HttpMethod.GET)
            Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response["status"])
        }

        @Test
        fun `Responds with Not Found when not found in concept catalog`() {
            val response = apiAuthorizedRequest("/catalogs/111222333/concepts/321", port, null, JwtToken("111222333").toString(), HttpMethod.GET)
            Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response["status"])
        }

        @Test
        fun `Responds with Not Found when not in relevant catalog`() {
            val response = apiAuthorizedRequest("/catalogs/333222111/concepts/123", port, null, JwtToken("333222111").toString(), HttpMethod.GET)
            Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response["status"])
        }

        @Test
        fun `Responds with OK when found in concept catalog`() {
            val response = apiAuthorizedRequest(path, port, null, JwtToken("111222333").toString(), HttpMethod.GET)
            Assertions.assertEquals(HttpStatus.OK.value(), response["status"])

            val result: Concept = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(MAPPED_DB_CONCEPT, result)
        }

    }

    @Nested
    internal inner class GetConcepts {
        private val path = "/catalogs/111222333/concepts"

        @Test
        fun `Responds with Unauthorized when missing token`() {
            val response = apiAuthorizedRequest(path, port, null, null, HttpMethod.GET)
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response["status"])
        }

        @Test
        fun `Responds with Forbidden when authorized for wrong scope`() {
            val response = apiAuthorizedRequest(path, port, null, JwtToken("111222333", "datanorge:test/wrong.scope").toString(), HttpMethod.GET)
            Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response["status"])
        }

        @Test
        fun `Responds with Forbidden when authorized for wrong catalog`() {
            val response = apiAuthorizedRequest(path, port, null, JwtToken("333222111").toString(), HttpMethod.GET)
            Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response["status"])
        }

        @Test
        fun `Responds with OK when authorized`() {
            val response = apiAuthorizedRequest(path, port, null, JwtToken("111222333").toString(), HttpMethod.GET)
            Assertions.assertEquals(HttpStatus.OK.value(), response["status"])

            val result: List<Concept> = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(listOf(MAPPED_DB_CONCEPT), result)
        }

    }

}
