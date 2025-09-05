package fr.avenirsesr.portfolio.file.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiFileEnumConfiguration {
  @Bean
  public OpenApiCustomizer fileEnumCustomizer() {
    return openApi -> {
      openApi
          .getComponents()
          .addSchemas("EFileType", SwaggerSchema.fileTypeSchema)
          .addSchemas("EUserPhotoType", SwaggerSchema.fileUserPhotoTypeSchema);
    };
  }
}
