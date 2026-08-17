package no.digdir.catalogview.elastic

import co.elastic.clients.transport.TransportOptions
import co.elastic.clients.transport.rest5_client.Rest5ClientOptions
import co.elastic.clients.transport.rest5_client.low_level.RequestOptions
import no.digdir.catalogview.config.ElasticProperties
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy
import org.apache.hc.core5.ssl.SSLContextBuilder
import org.apache.hc.core5.ssl.SSLContexts
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import java.io.File
import java.time.Duration
import javax.net.ssl.SSLContext

@Configuration
@EnableElasticsearchRepositories(basePackages = ["no.digdir.catalogview.elastic"])
open class ElasticsearchConfig(private val elasticProperties: ElasticProperties) : ElasticsearchConfiguration() {
    private fun sslContext(): SSLContext {
        val builder: SSLContextBuilder = SSLContexts.custom()

        builder.loadTrustMaterial(
            File(elasticProperties.storePath),
            elasticProperties.storePass.toCharArray(),
            TrustSelfSignedStrategy(),
        )

        return builder.build()
    }

    @Bean(name = ["elasticsearchClientConfiguration"])
    override fun clientConfiguration(): ClientConfiguration {
        val builder =
            ClientConfiguration
                .builder()
                .connectedTo(elasticProperties.host)

        if (elasticProperties.ssl) builder.usingSsl(sslContext())

        builder
            .withBasicAuth(elasticProperties.username, elasticProperties.password)
            .withConnectTimeout(Duration.ofSeconds(120))
            .withSocketTimeout(Duration.ofSeconds(120))

        return builder.build()
    }

    override fun transportOptions(): TransportOptions {
        val requestOptions =
            RequestOptions.DEFAULT
                .toBuilder()
                .addHeader("Accept", "application/vnd.elasticsearch+json;compatible-with=8")
                .addHeader("Content-Type", "application/vnd.elasticsearch+json;compatible-with=8")
                .build()

        return Rest5ClientOptions(requestOptions, false)
    }

    @Bean
    override fun elasticsearchCustomConversions(): ElasticsearchCustomConversions = ElasticsearchCustomConversions(
        listOf(
            FieldInterfaceReadConverter(),
            FieldInterfaceWriteConverter(),
        ),
    )
}
