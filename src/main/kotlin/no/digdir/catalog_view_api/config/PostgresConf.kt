package no.digdir.catalog_view_api.config

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
open class PostgresConf(
    private val postgresProperties: PostgresProperties
) {

    @Bean
    open fun conceptCatalogDataSource(): DataSource =
        DataSourceBuilder.create()
            .url(postgresProperties.conceptCatalog.url)
            .username(postgresProperties.conceptCatalog.username)
            .password(postgresProperties.conceptCatalog.password)
            .build()

    @Bean
    open fun adminServiceDataSource(): DataSource =
        DataSourceBuilder.create()
            .url(postgresProperties.adminService.url)
            .username(postgresProperties.adminService.username)
            .password(postgresProperties.adminService.password)
            .build()

    @Bean
    open fun conceptCatalogJdbc(conceptCatalogDataSource: DataSource): JdbcTemplate =
        JdbcTemplate(conceptCatalogDataSource)

    @Bean
    open fun adminServiceJdbc(adminServiceDataSource: DataSource): JdbcTemplate =
        JdbcTemplate(adminServiceDataSource)
}
