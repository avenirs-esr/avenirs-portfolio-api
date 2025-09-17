package fr.avenirsesr.portfolio.shared.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.common.openapi.infrastructure.adapter.BaseOpenApiConfiguration;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiSharedEnumConfiguration extends BaseOpenApiConfiguration {

  @Bean
  public OpenApiCustomizer portfolioEnumCustomizer() {
    return openApi -> {
      baseCustomization(openApi);
      openApi.getComponents().addSchemas("EPortfolioType", SwaggerSchema.portfolioTypeSchema);
    };
  }
}
