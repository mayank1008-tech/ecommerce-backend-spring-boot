package com.example.ecommerce.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        // Define the Security Scheme
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .name("Bearer Authentication")
                .scheme("bearer")
                .bearerFormat("JWT");

        // Add the Security Scheme to Components
        Components components = new Components()
                .addSecuritySchemes("Bearer Authentication", securityScheme);

        // Add the Security Requirement to the OpenAPI instance (Global)
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");

        // Return the OpenAPI object
        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("E-Commerce API")
                        .version("1.0")
                        .contact(new Contact().name("Support").email("admin@example.com"))
                        .description("API documentation with JWT Authentication support.")
                        .contact(new Contact()
                                .name("Mayank Jain")
                                .email("mj.mayank98@gmail.com")));
    }
}