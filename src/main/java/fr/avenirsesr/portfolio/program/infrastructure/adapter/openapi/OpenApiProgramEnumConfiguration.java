package fr.avenirsesr.portfolio.program.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiProgramEnumConfiguration {
  @Bean
  public OpenApiCustomizer programEnumCustomizer() {
    return openApi -> {
      openApi.getComponents().addSchemas("SkillLevelStatus", SwaggerSchema.skillLevelStatusSchema);
    };
  }
}
