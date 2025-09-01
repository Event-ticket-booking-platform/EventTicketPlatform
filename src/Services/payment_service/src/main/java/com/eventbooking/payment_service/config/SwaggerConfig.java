package com.eventbooking.payment_service.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI paymentOpenAPI() {
    // Gateway exposes payment routes under /payments/**
    return new OpenAPI()
        .servers(List.of(new Server().url("/payments")));
  }
}
