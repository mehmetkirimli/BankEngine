package com.BankEngine.config;


import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public GroupedOpenApi api()
  {
    return GroupedOpenApi.builder()
        .group("bank-engine")
        .addOpenApiCustomizer(openApi -> openApi.info(new Info()
            .title("Bank Engine API")
            .version("1.0")
            .description("High Performance Banking Transaction Engine")
        ))
        .pathsToMatch("/**")
        .build();
  }
}
