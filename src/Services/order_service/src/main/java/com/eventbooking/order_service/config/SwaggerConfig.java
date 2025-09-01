package com.eventbooking.order_service.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Force OpenAPI "servers" to the gateway path so Swagger UI calls the gateway,
 * not the internal Docker hostname.
 */
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI orderOpenAPI() {
    // Gateway exposes orders at /orders/**
    return new OpenAPI().servers(List.of(new Server().url("/orders")));
  }
}
