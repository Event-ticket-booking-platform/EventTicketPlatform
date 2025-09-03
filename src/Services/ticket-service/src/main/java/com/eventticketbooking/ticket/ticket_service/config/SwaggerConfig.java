package com.eventticketbooking.ticket.ticket_service.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI ticketOpenAPI() {
    // Gateway exposes TicketService under /tickets/**
    return new OpenAPI()
        .servers(List.of(new Server().url("/tickets")));
  }
}
