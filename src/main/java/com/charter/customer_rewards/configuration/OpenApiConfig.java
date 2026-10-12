package com.charter.customer_rewards.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures OpenAPI metadata for the rewards REST API.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates the OpenAPI description displayed in the API documentation.
     *
     * @return configured API metadata
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Customer Rewards Tracking Application API")
                        .version("1.0")
                        .description("Spring Boot REST API for calculating customer reward points"));
    }
}
