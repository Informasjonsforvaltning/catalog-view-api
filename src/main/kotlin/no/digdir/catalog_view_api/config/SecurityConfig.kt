package no.digdir.catalog_view_api.config

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.web.SecurityFilterChain

@Configuration
open class SecurityConfig {
    @Bean
    open fun filterChain(http: HttpSecurity, applicationProperties: ApplicationProperties): SecurityFilterChain {
        http.authorizeHttpRequests { authorize ->
            authorize.requestMatchers(HttpMethod.OPTIONS).permitAll()
                .requestMatchers(HttpMethod.GET, "/ping").permitAll()
                .requestMatchers(HttpMethod.GET, "/ready").permitAll()
                .requestMatchers(HttpMethod.GET, "/api-docs").permitAll()
                .requestMatchers(HttpMethod.GET, "/api-docs.yaml").permitAll()
                .anyRequest().hasAuthority("SCOPE_${applicationProperties.scope}") }
            .oauth2ResourceServer { resourceServer -> resourceServer.jwt() }
        return http.build()
    }

    @Bean
    open fun jwtDecoder(properties: OAuth2ResourceServerProperties): JwtDecoder {
        val jwtDecoder = NimbusJwtDecoder.withJwkSetUri(properties.jwt.jwkSetUri).build()
        jwtDecoder.setJwtValidator(
            DelegatingOAuth2TokenValidator(
                JwtTimestampValidator(),
                JwtIssuerValidator(properties.jwt.issuerUri)
            )
        )
        return jwtDecoder
    }

}
