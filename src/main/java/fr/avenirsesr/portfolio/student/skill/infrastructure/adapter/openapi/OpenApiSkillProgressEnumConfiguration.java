package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiSkillProgressEnumConfiguration {
  @Bean
  public OpenApiCustomizer skillProgressEnumCustomizer() {
    return openApi ->
        openApi.getComponents().addSchemas("ESkillLevel", SwaggerSchema.skillLevelSchema);
  }
}
