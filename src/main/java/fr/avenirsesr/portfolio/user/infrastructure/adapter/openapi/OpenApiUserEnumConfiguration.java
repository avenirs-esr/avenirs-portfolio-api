package fr.avenirsesr.portfolio.user.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiUserEnumConfiguration {
  @Bean
  public OpenApiCustomizer userEnumCustomizer() {
    return openApi -> {
      openApi
          .getComponents()
          .addSchemas("EContextType", SwaggerSchema.userContextSchema)
          .addSchemas("EUserCategory", SwaggerSchema.userCategorySchema)
          .addSchemas("ERole", SwaggerSchema.roleSchema);
    };
  }
}
