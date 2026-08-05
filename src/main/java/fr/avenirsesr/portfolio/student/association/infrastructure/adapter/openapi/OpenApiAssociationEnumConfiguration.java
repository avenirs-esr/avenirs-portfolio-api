package fr.avenirsesr.portfolio.student.association.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiAssociationEnumConfiguration {
  @Bean
  public OpenApiCustomizer associationEnumCustomizer() {
    return openApi ->
        openApi
            .getComponents()
            .addSchemas("EAssociationContextType", SwaggerSchema.associationContextTypeSchema);
  }
}
