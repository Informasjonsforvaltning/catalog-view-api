package no.digdir.catalog_view_api.utils

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import no.digdir.catalog_view_api.model.InternalConcept
import no.digdir.catalog_view_api.utils.ApiTestContext.Companion.mongoContainer
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate


fun apiGet(port: Int, endpoint: String, acceptHeader: String?): Map<String, Any> {

    return try {
        val connection = URL("http://localhost:$port$endpoint").openConnection() as HttpURLConnection
        if (acceptHeader != null) connection.setRequestProperty("Accept", acceptHeader)
        connection.connect()

        if (isOK(connection.responseCode)) {
            val responseBody = connection.inputStream.bufferedReader().use(BufferedReader::readText)
            mapOf(
                "body" to responseBody,
                "header" to connection.headerFields.toString(),
                "status" to connection.responseCode
            )
        } else {
            mapOf(
                "status" to connection.responseCode,
                "header" to " ",
                "body" to " "
            )
        }
    } catch (e: Exception) {
        mapOf(
            "status" to e.toString(),
            "header" to " ",
            "body" to " "
        )
    }
}

fun apiAuthorizedRequest(
    path: String, port: Int, body: String?, token: String?, httpMethod: HttpMethod,
    accept: MediaType = MediaType.APPLICATION_JSON
): Map<String, Any> {


    val request = RestTemplate()
    request.requestFactory = HttpComponentsClientHttpRequestFactory()
    val url = "http://localhost:$port$path"
    val headers = HttpHeaders()
    headers.accept = listOf(accept)
    token?.let { headers.setBearerAuth(it) }
    headers.contentType = MediaType.APPLICATION_JSON
    val entity: HttpEntity<String> = HttpEntity(body, headers)

    return try {
        val response = request.exchange(url, httpMethod, entity, String::class.java)
        mapOf(
            "body" to response.body,
            "header" to response.headers,
            "status" to response.statusCode.value()
        )

    } catch (e: HttpClientErrorException) {
        mapOf(
            "status" to e.statusCode.value(),
            "header" to " ",
            "body" to e.toString()
        )
    } catch (e: Exception) {
        mapOf(
            "status" to e.toString(),
            "header" to " ",
            "body" to " "
        )
    }
}

private fun isOK(response: Int?): Boolean =
    if (response == null) false
    else HttpStatus.resolve(response)?.is2xxSuccessful == true

private fun InternalConcept.mongoDocument(): Document {
    val concept = Document()
    concept.append("_id", id)
    concept.append("originaltBegrep", originaltBegrep)
    concept.append("erPublisert", erPublisert)
    concept.append("statusURI", statusURI)

    val semver = Document()
    semver.append("major", versjonsnr.major)
    semver.append("minor", versjonsnr.minor)
    semver.append("patch", versjonsnr.patch)
    concept.append("versjonsnr", semver)

    val org = Document()
    org.append("_id", ansvarligVirksomhet.id)
    org.append("uri", ansvarligVirksomhet.uri)
    org.append("navn", ansvarligVirksomhet.navn)
    org.append("orgPath", ansvarligVirksomhet.orgPath)
    org.append("prefLabel", ansvarligVirksomhet.prefLabel)
    concept.append("ansvarligVirksomhet", org)

    return concept
}

fun populateDB() {
    val connectionString = ConnectionString("mongodb://${MONGO_USER}:${MONGO_PASSWORD}@localhost:${mongoContainer.getMappedPort(MONGO_PORT)}/?authSource=admin&authMechanism=SCRAM-SHA-1")
    val pojoCodecRegistry = CodecRegistries.fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(
            PojoCodecProvider.builder().automatic(true).build()))

    val client: MongoClient = MongoClients.create(connectionString)

    val conceptCatalogDatabase = client.getDatabase("concept-catalogue").withCodecRegistry(pojoCodecRegistry)
    val conceptCatalogCollection = conceptCatalogDatabase.getCollection("begrep")
    conceptCatalogCollection.insertOne(DB_CONCEPT.mongoDocument())

    client.close()
}
