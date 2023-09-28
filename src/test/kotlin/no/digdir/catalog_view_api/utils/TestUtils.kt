package no.digdir.catalog_view_api.utils

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import no.digdir.catalog_view_api.model.AdminCode
import no.digdir.catalog_view_api.model.AdminUser
import no.digdir.catalog_view_api.model.BegrepsRelasjon
import no.digdir.catalog_view_api.model.CodeList
import no.digdir.catalog_view_api.model.Definisjon
import no.digdir.catalog_view_api.model.EditableFields
import no.digdir.catalog_view_api.model.Field
import no.digdir.catalog_view_api.model.InternalConcept
import no.digdir.catalog_view_api.model.InterntFelt
import no.digdir.catalog_view_api.model.LocalizedStrings
import no.digdir.catalog_view_api.model.URITekst
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

    val preferred = Document()
    preferred.append("navn", anbefaltTerm?.navn)
    concept.append("anbefaltTerm", preferred)
    concept.append("tillattTerm", tillattTerm)
    concept.append("frarådetTerm", frarådetTerm)

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

    concept.append("definisjon", definisjon?.mongoDocument())
    concept.append("folkeligForklaring", folkeligForklaring?.mongoDocument())
    concept.append("rettsligForklaring", rettsligForklaring?.mongoDocument())

    concept.append("merknad", merknad)
    concept.append("eksempel", eksempel)
    concept.append("omfang", omfang?.mongoDocument())

    val contact = Document()
    contact.append("harEpost", kontaktpunkt?.harEpost)
    contact.append("harTelefon", kontaktpunkt?.harTelefon)
    concept.append("kontaktpunkt", contact)
    concept.append("abbreviatedLabel", abbreviatedLabel)
    concept.append("gyldigTom", gyldigTom)
    concept.append("gyldigFom", gyldigFom)

    val edited = Document()
    edited.append("endretAv", endringslogelement?.endretAv)
    edited.append("endringstidspunkt", endringslogelement?.endringstidspunkt)
    concept.append("endringslogelement", edited)
    concept.append("opprettetAv", opprettetAv)
    concept.append("opprettet", opprettet)

    concept.append("interneFelt", interneFelt?.mapValues { it.value.mongoDocument() })
    concept.append("fagområdeKoder", fagområdeKoder)
    concept.append("assignedUser", assignedUser)

    concept.append("seOgså", seOgså)
    concept.append("erstattesAv", erstattesAv)
    concept.append("begrepsRelasjon", begrepsRelasjon?.map { it.mongoDocument() })

    return concept
}

private fun Definisjon.mongoDocument(): Document {
    val definition = Document()
    definition.append("tekst", tekst)

    val source = Document()
    source.append("forholdTilKilde", kildebeskrivelse?.forholdTilKilde)
    source.append("kilde", kildebeskrivelse?.kilde?.map { it.mongoDocument() })
    definition.append("kildebeskrivelse", source)
    return definition
}

private fun URITekst.mongoDocument(): Document {
    val doc = Document()
    doc.append("uri", uri)
    doc.append("tekst", tekst)
    return doc
}

private fun InterntFelt.mongoDocument(): Document {
    val field = Document()
    field.append("value", value)
    return field
}

private fun LocalizedStrings.mongoDocument(): Document {
    val strings = Document()
    strings.append("nb", nb)
    strings.append("nn", nn)
    strings.append("en", en)
    return strings
}

private fun Field.mongoDocument(): Document {
    val field = Document()
    field.append("_id", id)
    field.append("label", label.mongoDocument())
    field.append("description", description.mongoDocument())
    field.append("catalogId", catalogId)
    field.append("type", type)
    field.append("codeListId", codeListId)
    return field
}

private fun AdminCode.mongoDocument(): Document {
    val list = Document()
    list.append("_id", id)
    list.append("parentID", parentID)
    list.append("name", name.mongoDocument())
    return list
}

private fun CodeList.mongoDocument(): Document {
    val list = Document()
    list.append("_id", id)
    list.append("catalogId", catalogId)
    list.append("codes", codes.map { it.mongoDocument() })
    return list
}

private fun AdminUser.mongoDocument(): Document {
    val list = Document()
    list.append("_id", id)
    list.append("catalogId", catalogId)
    list.append("name", name)
    list.append("email", email)
    list.append("telephoneNumber", telephoneNumber)
    return list
}

private fun EditableFields.mongoDocument(): Document {
    val fields = Document()
    fields.append("_id", catalogId)
    fields.append("domainCodeListId", domainCodeListId)
    return fields
}

private fun BegrepsRelasjon.mongoDocument(): Document {
    val relation = Document()
    relation.append("relasjon", relasjon)
    relation.append("relasjonsType", relasjonsType)
    relation.append("beskrivelse", beskrivelse)
    relation.append("inndelingskriterium", inndelingskriterium)
    relation.append("relatertBegrep", relatertBegrep)
    return relation
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

    val adminDatabase = client.getDatabase("catalogAdminService").withCodecRegistry(pojoCodecRegistry)
    val internalFieldsCollection = adminDatabase.getCollection("internalFields")
    internalFieldsCollection.insertMany(DB_INTERNAL_FIELDS.map { it.mongoDocument() })
    val codeListsCollection = adminDatabase.getCollection("codeLists")
    codeListsCollection.insertMany(DB_CODE_LISTS.map { it.mongoDocument() })
    val usersCollection = adminDatabase.getCollection("users")
    usersCollection.insertMany(DB_ADMIN_USERS.map { it.mongoDocument() })
    val editableFieldsCollection = adminDatabase.getCollection("editableFields")
    editableFieldsCollection.insertMany(DB_EDITABLE_FIELDS.map { it.mongoDocument() })

    client.close()
}
