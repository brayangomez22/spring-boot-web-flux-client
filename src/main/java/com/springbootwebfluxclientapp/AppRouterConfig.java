package com.springbootwebfluxclientapp;

import com.springbootwebfluxclientapp.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AppRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ProductHandler handler) {
        return route(GET("/api/client"), handler::findAll)
                .andRoute(GET("/api/client/{id}"), handler::findById)
                .andRoute(POST("/api/client"), handler::create)
                .andRoute(PUT("/api/client/{id}"), handler::update)
                .andRoute(DELETE("/api/client/{id}"), handler::delete)
                .andRoute(POST("/api/client/upload/{id}"), handler::upload);
    }
}
