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
public class UserServiceRoutes {

    @Bean
    public RouterFunction<ServerResponse> userRoutes() {
        return route()
                .route(RequestPredicates.path("/api/v1/user/**"), http())
                .before(uri("http://localhost:8080"))
                .build();
    }
}