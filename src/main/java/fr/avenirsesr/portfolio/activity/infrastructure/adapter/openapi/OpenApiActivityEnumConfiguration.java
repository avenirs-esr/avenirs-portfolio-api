package fr.avenirsesr.portfolio.activity.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiActivityEnumConfiguration {
  @Bean
  public OpenApiCustomizer activityEnumCustomizer() {
    return openApi -> {
      openApi
          .getComponents()
          .addSchemas("EActivityThematic", SwaggerSchema.activityThematicSchema)
          .addSchemas("EActivityStatus", SwaggerSchema.activityStatusSchema);
    };
  }
}
