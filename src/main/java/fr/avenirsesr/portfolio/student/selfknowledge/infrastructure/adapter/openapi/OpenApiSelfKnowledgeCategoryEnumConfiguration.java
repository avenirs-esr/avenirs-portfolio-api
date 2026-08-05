package fr.avenirsesr.portfolio.student.selfknowledge.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiSelfKnowledgeCategoryEnumConfiguration {
  @Bean
  public OpenApiCustomizer selfKnowledgeCategoryEnumCustomizer() {
    return openApi -> {
      openApi
          .getComponents()
          .addSchemas("ESelfKnowledgeCategory", SwaggerSchema.selfKnowledgeCategoryTypeSchema);
    };
  }
}
