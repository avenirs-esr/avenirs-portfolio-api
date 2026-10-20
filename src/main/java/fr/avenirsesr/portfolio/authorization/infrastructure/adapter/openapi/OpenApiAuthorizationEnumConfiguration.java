package fr.avenirsesr.portfolio.authorization.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiAuthorizationEnumConfiguration {
  @Bean
  public OpenApiCustomizer authorizationEnumCustomizer() {
    return openApi -> {
      openApi
          .getComponents()
          .addSchemas("EAuthorization", SwaggerSchema.authorizationSchema)
          .addSchemas("EAuthorizationEntityType", SwaggerSchema.authorizationEntityTypeSchema);
    };
  }
}
