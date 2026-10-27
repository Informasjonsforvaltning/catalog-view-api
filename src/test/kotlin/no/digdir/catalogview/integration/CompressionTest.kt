package no.digdir.catalogview.integration

import no.digdir.catalogview.elastic.ElasticUpdater
import no.digdir.catalogview.utils.ApiTestContext
import no.digdir.catalogview.utils.jwk.JwtToken
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Verifies, through the real embedded server, that responses over the configured
 * threshold are gzipped when the client accepts it and left alone otherwise.
 *
 * Every endpoint with a body requires a Maskinporten token, so the request is
 * authorized the same way ConceptsTest does it.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    properties = ["spring.profiles.active=test"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("integration")
class CompressionTest : ApiTestContext() {
    @Autowired
    private lateinit var elasticUpdater: ElasticUpdater

    // java.net.http never decompresses on its own, so the raw bytes and headers reach the assertions.
    private val client = HttpClient.newHttpClient()

    @BeforeAll
    fun populateElastic() {
        elasticUpdater.updateConceptsViewIndex()
    }

    private fun get(acceptEncoding: String?): HttpResponse<ByteArray> {
        val builder =
            HttpRequest
                .newBuilder(URI("http://localhost:$port/catalogs/111222333/concepts"))
                .header("Authorization", "Bearer ${JwtToken("111222333")}")
                .GET()
        if (acceptEncoding != null) builder.header("Accept-Encoding", acceptEncoding)
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray())
    }

    @Test
    fun `gzips the response when the client accepts it`() {
        val response = get("gzip")
        assertEquals(200, response.statusCode())
        assertEquals("gzip", response.headers().firstValue("Content-Encoding").orElse(null))
        assertTrue(response.headers().allValues("Vary").joinToString(",").lowercase().contains("accept-encoding"))
        val body = response.body()
        assertTrue(body.size > 2 && body[0] == 0x1f.toByte() && body[1] == 0x8b.toByte(), "body is not gzip")
    }

    @Test
    fun `leaves the response uncompressed when the client does not ask`() {
        val response = get(null)
        assertEquals(200, response.statusCode())
        assertTrue(response.headers().firstValue("Content-Encoding").isEmpty)
        assertTrue(response.body().size > 1024, "test endpoint must exceed the 1 KB threshold")
    }
}
