package fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiDeclaredExperienceEnumConfiguration {
  @Bean
  public OpenApiCustomizer declaredExperienceEnumCustomizer() {
    return openApi -> {
      openApi.getComponents().addSchemas("EExperienceType", SwaggerSchema.experienceTypeSchema);
    };
  }
}
