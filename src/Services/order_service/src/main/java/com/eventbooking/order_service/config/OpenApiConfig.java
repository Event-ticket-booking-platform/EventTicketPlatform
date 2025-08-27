package com.eventbooking.order_service.config;


import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OrderService")
                        .version("v1")
                        .description("API documentation for the Order Service"))
                .servers(List.of(
                        new Server().url("/orders").description("Order Service through API Gateway")
                ));
    }
}
