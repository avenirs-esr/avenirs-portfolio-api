package fr.avenirsesr.portfolio.shared.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiSharedEnumConfiguration {
  @Bean
  public OpenApiCustomizer sharedEnumCustomizer() {
    return openApi -> {
      openApi
          .getComponents()
          .addSchemas("EDurationUnit", SwaggerSchema.sharedDurationUnitSchema)
          .addSchemas("EErrorCode", SwaggerSchema.sharedErrorCodeSchema)
          .addSchemas("ELanguage", SwaggerSchema.sharedLanguageSchema)
          .addSchemas("EPortfolioType", SwaggerSchema.sharedPortfolioTypeSchema)
          .addSchemas("ESortField", SwaggerSchema.sharedSortFieldSchema)
          .addSchemas("ESortOrder", SwaggerSchema.sharedSortOrderSchema);
    };
  }
}
