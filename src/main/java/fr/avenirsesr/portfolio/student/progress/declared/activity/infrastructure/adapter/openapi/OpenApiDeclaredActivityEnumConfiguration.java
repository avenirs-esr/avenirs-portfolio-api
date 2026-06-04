package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiDeclaredActivityEnumConfiguration {
  @Bean
  public OpenApiCustomizer declaredActivityEnumCustomize() {
    return openApi ->
        openApi
            .getComponents()
            .addSchemas("EDeclaredActivityStatus", SwaggerSchema.declaredActivityStatusSchema)
            .addSchemas("EFeedbackStatus", SwaggerSchema.feedbackStatusSchema);
  }
}
