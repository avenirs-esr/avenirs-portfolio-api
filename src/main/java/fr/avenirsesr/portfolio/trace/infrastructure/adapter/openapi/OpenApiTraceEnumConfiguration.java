package fr.avenirsesr.portfolio.trace.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiTraceEnumConfiguration {
  @Bean
  public OpenApiCustomizer traceEnumCustomizer() {
    return openApi -> {
      openApi.getComponents().addSchemas("ETraceStatus", SwaggerSchema.traceStatusSchema);
      openApi.getComponents().addSchemas("ETraceAuthorType", SwaggerSchema.traceAuthorTypeSchema);
    };
  }
}
