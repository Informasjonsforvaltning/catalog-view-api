package no.digdir.catalog_view_api.utils

import no.digdir.catalog_view_api.model.AdminUser
import no.digdir.catalog_view_api.model.CodeList
import no.digdir.catalog_view_api.model.EditableFields
import no.digdir.catalog_view_api.model.Field
import no.digdir.catalog_view_api.model.InternalConcept
import no.digdir.catalog_view_api.utils.ApiTestContext.Companion.adminContainer
import no.digdir.catalog_view_api.utils.ApiTestContext.Companion.conceptContainer
import org.postgresql.util.PGobject
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.sql.DriverManager


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
            "body" to (response.body ?: ""),
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

private val objectMapper = jacksonObjectMapper()

private fun jsonb(value: Any?): PGobject {
    val pg = PGobject()
    pg.type = "jsonb"
    pg.value = if (value != null) objectMapper.writeValueAsString(value) else null
    return pg
}

fun populateDB() {
    populateConceptCatalog()
    populateAdminService()
}

private fun populateConceptCatalog() {
    val conn = DriverManager.getConnection(
        conceptContainer.getJdbcUrl(),
        conceptContainer.getUsername(),
        conceptContainer.getPassword()
    )
    conn.use { c ->
        c.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS concepts (
                id                       VARCHAR(255) PRIMARY KEY,
                originalt_begrep         VARCHAR(255) NOT NULL,
                ansvarlig_virksomhet_id  VARCHAR(255) NOT NULL,
                status                   VARCHAR(50),
                er_publisert             BOOLEAN DEFAULT FALSE,
                is_archived              BOOLEAN DEFAULT FALSE,
                data                     JSONB NOT NULL
            )
        """)

        val stmt = c.prepareStatement(
            "INSERT INTO concepts (id, originalt_begrep, ansvarlig_virksomhet_id, status, er_publisert, is_archived, data) VALUES (?, ?, ?, ?, ?, ?, ?)"
        )
        insertConcept(stmt, DB_CONCEPT)
        stmt.executeBatch()
    }
}

private fun insertConcept(stmt: java.sql.PreparedStatement, concept: InternalConcept) {
    stmt.setString(1, concept.id)
    stmt.setString(2, concept.originaltBegrep)
    stmt.setString(3, concept.ansvarligVirksomhet.id)
    stmt.setString(4, null)
    stmt.setBoolean(5, concept.erPublisert)
    stmt.setBoolean(6, false)
    stmt.setObject(7, jsonb(concept))
    stmt.addBatch()
}

private fun populateAdminService() {
    val conn = DriverManager.getConnection(
        adminContainer.getJdbcUrl(),
        adminContainer.getUsername(),
        adminContainer.getPassword()
    )
    conn.use { c ->
        c.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS catalog_users (
                id                VARCHAR(255) PRIMARY KEY,
                catalog_id        VARCHAR(255) NOT NULL,
                name              VARCHAR(500) NOT NULL,
                email             VARCHAR(500),
                telephone_number  VARCHAR(100)
            )
        """)
        c.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS editable_fields (
                catalog_id          VARCHAR(255) PRIMARY KEY,
                domain_code_list_id VARCHAR(255)
            )
        """)
        c.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS internal_fields (
                id              VARCHAR(255) PRIMARY KEY,
                catalog_id      VARCHAR(255) NOT NULL,
                label           JSONB,
                description     JSONB,
                type            VARCHAR(50) NOT NULL,
                location        VARCHAR(50) NOT NULL,
                code_list_id    VARCHAR(255),
                enable_filter   BOOLEAN
            )
        """)
        c.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS code_lists (
                id          VARCHAR(255) PRIMARY KEY,
                name        VARCHAR(500) NOT NULL,
                catalog_id  VARCHAR(255) NOT NULL,
                description VARCHAR(2000) NOT NULL,
                codes       JSONB
            )
        """)

        val userStmt = c.prepareStatement(
            "INSERT INTO catalog_users (id, catalog_id, name, email, telephone_number) VALUES (?, ?, ?, ?, ?)"
        )
        DB_ADMIN_USERS.forEach { u ->
            userStmt.setString(1, u.id)
            userStmt.setString(2, u.catalogId)
            userStmt.setString(3, u.name)
            userStmt.setString(4, u.email)
            userStmt.setString(5, u.telephoneNumber)
            userStmt.addBatch()
        }
        userStmt.executeBatch()

        val efStmt = c.prepareStatement(
            "INSERT INTO editable_fields (catalog_id, domain_code_list_id) VALUES (?, ?)"
        )
        DB_EDITABLE_FIELDS.forEach { ef ->
            efStmt.setString(1, ef.catalogId)
            efStmt.setString(2, ef.domainCodeListId)
            efStmt.addBatch()
        }
        efStmt.executeBatch()

        val ifStmt = c.prepareStatement(
            "INSERT INTO internal_fields (id, catalog_id, label, description, type, location, code_list_id, enable_filter) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        )
        DB_INTERNAL_FIELDS.forEach { f ->
            ifStmt.setString(1, f.id)
            ifStmt.setString(2, f.catalogId)
            ifStmt.setObject(3, jsonb(f.label))
            ifStmt.setObject(4, jsonb(f.description))
            ifStmt.setString(5, f.type)
            ifStmt.setString(6, "main_column")
            ifStmt.setString(7, f.codeListId)
            ifStmt.setNull(8, java.sql.Types.BOOLEAN)
            ifStmt.addBatch()
        }
        ifStmt.executeBatch()

        val clStmt = c.prepareStatement(
            "INSERT INTO code_lists (id, name, catalog_id, description, codes) VALUES (?, ?, ?, ?, ?)"
        )
        DB_CODE_LISTS.forEach { cl ->
            clStmt.setString(1, cl.id)
            clStmt.setString(2, "")
            clStmt.setString(3, cl.catalogId)
            clStmt.setString(4, "")
            clStmt.setObject(5, jsonb(cl.codes))
            clStmt.addBatch()
        }
        clStmt.executeBatch()
    }
}
