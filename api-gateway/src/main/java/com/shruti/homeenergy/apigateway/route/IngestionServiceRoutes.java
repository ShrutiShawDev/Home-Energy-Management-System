package com.shruti.homeenergy.apigateway.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
public class IngestionServiceRoutes {

    @Bean
    public RouterFunction<ServerResponse> ingestionRoutes() {
        return route()
                .route(RequestPredicates.path("/api/v1/ingestion/**"), http())
                .before(uri("http://localhost:8082"))
                .build();
    }
}