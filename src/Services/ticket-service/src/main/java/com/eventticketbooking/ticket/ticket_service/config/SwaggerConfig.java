package com.eventticketbooking.ticket.ticket_service.config;

// import io.swagger.v3.oas.models.OpenAPI;
// import io.swagger.v3.oas.models.servers.Server;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import java.util.List;

// @Configuration
// public class SwaggerConfig {

//   @Bean
//   public OpenAPI ticketOpenAPI() {
//     // Gateway exposes TicketService under /tickets/**
//     return new OpenAPI()
//         .servers(List.of(new Server().url("/tickets")));
//   }
// }
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
  public OpenAPI ticketOpenAPI() {
    // Gateway exposes tickets at /tickets/**
    return new OpenAPI().servers(List.of(new Server().url("/tickets")));
  }
}

