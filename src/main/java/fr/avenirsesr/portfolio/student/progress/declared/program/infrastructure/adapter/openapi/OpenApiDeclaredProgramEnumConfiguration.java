package fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiDeclaredProgramEnumConfiguration {
  @Bean
  public OpenApiCustomizer declaredProgramEnumCustomizer() {
    return openApi -> {
      openApi.getComponents().addSchemas("EProgramStatus", SwaggerSchema.programStatusSchema);
    };
  }
}
