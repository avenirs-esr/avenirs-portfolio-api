package fr.avenirsesr.portfolio.ams.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiAmsEnumConfiguration {
  @Bean
  public OpenApiCustomizer amsEnumCustomizer() {
    return openApi -> {
      openApi.getComponents().addSchemas("AmsStatus", SwaggerSchema.amsStatusSchema);
    };
  }
}
