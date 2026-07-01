package com.shruti.homeenergy.apigateway.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Value("${keycloak.auth.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${security.excluded.urls}")
    private String[] excludedUrls;


    @PostConstruct
    public void logExcluded() {
        System.out.println("Excluded URLs: " + Arrays.toString(excludedUrls));
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        return http.authorizeHttpRequests(h ->
                h.requestMatchers(excludedUrls).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()) )
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(){
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}
