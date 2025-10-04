package com.vuong.vmess.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${env.url}")
    private String ENV_URL;

    @Bean
    public OpenAPI customOpenAPI() {
        String API_KEY = "Bearer Token";
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url(ENV_URL)
                                .description("HTTPS server")
                ))

                .info(new Info()
                        .title("Project Base Spring API")
                        .version("1.0")
                        .description("Documentation Project Base Spring API v1.0")
                )

                .components(new Components()
                        .addSecuritySchemes(
                                API_KEY,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .scheme("Bearer")
                                        .bearerFormat("JWT")
                                        .type(SecurityScheme.Type.HTTP)
                                        .in(SecurityScheme.In.HEADER)
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(API_KEY));
    }

}
