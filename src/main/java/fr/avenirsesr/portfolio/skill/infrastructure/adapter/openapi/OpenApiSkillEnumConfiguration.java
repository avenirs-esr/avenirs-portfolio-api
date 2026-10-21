package fr.avenirsesr.portfolio.skill.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiSkillEnumConfiguration {
  @Bean
  public OpenApiCustomizer skillEnumCustomizer() {
    return openApi ->
        openApi
            .getComponents()
            .addSchemas("ESkillReferentialType", SwaggerSchema.skillReferentialTypeSchema)
            .addSchemas("ESkillKind", SwaggerSchema.skillKindSchema);
  }
}
