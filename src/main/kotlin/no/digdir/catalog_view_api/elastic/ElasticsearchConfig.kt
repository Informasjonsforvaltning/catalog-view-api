package no.digdir.catalog_view_api.elastic

import no.digdir.catalog_view_api.config.ElasticProperties
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.ssl.SSLContextBuilder
import org.apache.http.ssl.SSLContexts
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.time.Duration
import javax.net.ssl.SSLContext

@Configuration
@EnableElasticsearchRepositories(basePackages = ["no.digdir.catalog_view_api.elastic"])
open class ElasticsearchConfig(private val elasticProperties: ElasticProperties): ElasticsearchConfiguration() {

    private fun sslContext(): SSLContext {
        val builder: SSLContextBuilder = SSLContexts.custom()

        val factory = CertificateFactory.getInstance("X.509")
        val cert: Certificate = factory.generateCertificate(
            FileInputStream(elasticProperties.certPath)
        )

        val keyStorePass = elasticProperties.storePass.toCharArray()
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null)
        keyStore.setCertificateEntry("es-http-public", cert)
        keyStore.store(FileOutputStream(elasticProperties.storeName), keyStorePass)

        builder.loadTrustMaterial(
            File(elasticProperties.storeName),
            keyStorePass,
            TrustSelfSignedStrategy()
        )

        return builder.build()
    }

    @Bean(name = ["elasticsearchClientConfiguration"])
    override fun clientConfiguration(): ClientConfiguration {
        val builder = ClientConfiguration.builder()
            .connectedTo(elasticProperties.host)

        if (elasticProperties.ssl) builder.usingSsl(sslContext())

        builder.withBasicAuth(elasticProperties.username, elasticProperties.password)
            .withConnectTimeout(Duration.ofSeconds(120))

        return builder.build()
    }

}
