package com.aquariux.technical.assessment.trade.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tradeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trade API")
                        .description("Aquariux Technical Assessment - Trade Application API")
                        .version("2025.11.18"));
    }
}