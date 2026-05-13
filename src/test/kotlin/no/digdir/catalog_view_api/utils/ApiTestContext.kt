package no.digdir.catalog_view_api.utils

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.elasticsearch.ElasticsearchContainer
import java.net.HttpURLConnection
import java.net.URL

class KPostgreSQLContainer(imageName: String) : PostgreSQLContainer<KPostgreSQLContainer>(imageName)

abstract class ApiTestContext {

    @LocalServerPort
    var port = 0

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "application.postgresql.conceptCatalog.url=${conceptContainer.getJdbcUrl()}",
                "application.postgresql.conceptCatalog.username=${conceptContainer.getUsername()}",
                "application.postgresql.conceptCatalog.password=${conceptContainer.getPassword()}",
                "application.postgresql.adminService.url=${adminContainer.getJdbcUrl()}",
                "application.postgresql.adminService.username=${adminContainer.getUsername()}",
                "application.postgresql.adminService.password=${adminContainer.getPassword()}",
                "application.elastic.host=localhost:${elasticContainer.getMappedPort(9200)}"
            ).applyTo(configurableApplicationContext.environment)
        }
    }

    companion object {
        val conceptContainer: KPostgreSQLContainer = KPostgreSQLContainer("postgres:16")
            .withDatabaseName("concept_catalog")
            .withUsername("testuser")
            .withPassword("testpassword")

        val adminContainer: KPostgreSQLContainer = KPostgreSQLContainer("postgres:16")
            .withDatabaseName("catalog_admin_service")
            .withUsername("testuser")
            .withPassword("testpassword")

        val elasticContainer: ElasticsearchContainer = ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.17.2")
            .withEnv(ELASTIC_ENV_VALUES)

        init {
            startMockServer()
            conceptContainer.start()
            adminContainer.start()
            elasticContainer.start()
            populateDB()

            try {
                val con = URL("http://localhost:5050/ping").openConnection() as HttpURLConnection
                con.connect()
                if (con.responseCode != 200) {
                    stopMockServer()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stopMockServer()
            }
        }
    }

}
