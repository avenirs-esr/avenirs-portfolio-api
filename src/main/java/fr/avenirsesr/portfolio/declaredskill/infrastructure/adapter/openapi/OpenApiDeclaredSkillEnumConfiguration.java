package fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiDeclaredSkillEnumConfiguration {
  @Bean
  public OpenApiCustomizer declaredSkillEnumCustomizer() {
    return openApi -> {
      openApi
          .getComponents()
          .addSchemas("EExternalSkillCategoryType", SwaggerSchema.externalSkillCategoryTypeSchema)
          .addSchemas("EDeclaredSkillLevel", SwaggerSchema.declaredSkillLevelSchema)
          .addSchemas("EExternalSkillType", SwaggerSchema.externalSkillTypeSchema);
    };
  }
}
