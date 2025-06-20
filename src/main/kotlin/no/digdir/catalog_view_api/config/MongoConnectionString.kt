package no.digdir.catalog_view_api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URLEncoder

@ConfigurationProperties("spring.data.mongodb")
data class MongoConnectionString(
    val host: String,
    val port: String,
    val username: String,
    val password: String,
    val authenticationDatabase: String,
    val replicaSetName: String
) {

    fun uri(): String {
        val uriBuilder = StringBuilder()
        uriBuilder.append("mongodb://")
        uriBuilder.append(URLEncoder.encode(username, "utf-8"))
        uriBuilder.append(":")
        uriBuilder.append(URLEncoder.encode(password, "utf-8"))
        uriBuilder.append("@")
        uriBuilder.append(host)
        uriBuilder.append(":")
        uriBuilder.append(port)
        uriBuilder.append("?authSource=")
        uriBuilder.append(authenticationDatabase)
        if (replicaSetName != "test") {
            uriBuilder.append("&replicaSet=")
            uriBuilder.append(replicaSetName)
        }
        return uriBuilder.toString()
    }

}
