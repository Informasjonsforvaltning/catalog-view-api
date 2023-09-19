package no.digdir.catalog_view_api.utils

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.net.HttpURLConnection
import java.net.URL

abstract class ApiTestContext {

    @LocalServerPort
    var port = 0

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.data.mongodb.uri=mongodb://${MONGO_USER}:${MONGO_PASSWORD}@localhost:${mongoContainer.getMappedPort(MONGO_PORT)}/?authSource=admin&authMechanism=SCRAM-SHA-1"
            ).applyTo(configurableApplicationContext.environment)
        }
    }

    companion object {
        var mongoContainer: KGenericContainer

        init {
            startMockServer()

            mongoContainer = KGenericContainer("mongo:latest")
                .withEnv(MONGO_ENV_VALUES)
                .withExposedPorts(MONGO_PORT)
                .waitingFor(Wait.forListeningPort())

            mongoContainer.start()

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

class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)
