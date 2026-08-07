package fr.avenirsesr.portfolio.shared.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.common.openapi.infrastructure.adapter.BaseSwaggerSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiLanguageEnumConfiguration {
  @Bean
  public OpenApiCustomizer languageEnumCustomizer() {
    return openApi -> {
      openApi
          .getComponents()
          .addSchemas("ELanguage", BaseSwaggerSchema.languageSchema)
          .addSchemas("EErrorCode", BaseSwaggerSchema.errorCodeSchema);
    };
  }
}
