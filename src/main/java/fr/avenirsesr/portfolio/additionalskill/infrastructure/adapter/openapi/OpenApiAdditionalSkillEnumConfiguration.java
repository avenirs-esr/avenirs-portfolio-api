package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiAdditionalSkillEnumConfiguration {
  @Bean
  public OpenApiCustomizer additionalSkillEnumCustomizer() {
    return openApi -> {
      openApi
          .getComponents()
          .addSchemas(
              "EAdditionalSkillCategoryType", SwaggerSchema.additionalSkillCategoryTypeSchema)
          .addSchemas("EAdditionalSkillLevel", SwaggerSchema.additionalSkillLevelSchema)
          .addSchemas("EAdditionalSkillType", SwaggerSchema.additionalSkillTypeSchema);
    };
  }
}
