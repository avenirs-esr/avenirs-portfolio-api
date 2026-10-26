package fr.avenirsesr.portfolio.user.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.common.openapi.infrastructure.adapter.BaseSwaggerSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiUserEnumConfiguration {
  @Bean
  public OpenApiCustomizer userEnumCustomizer() {
    return openApi -> {
      openApi
          .getComponents()
          .addSchemas("EContextType", SwaggerSchema.userContextSchema)
          .addSchemas("EUserCategory", BaseSwaggerSchema.userCategorySchema)
          .addSchemas("ERole", BaseSwaggerSchema.roleSchema)
          .addSchemas("EUniverse", BaseSwaggerSchema.universeSchema)
          .addSchemas("EInstitutionType", SwaggerSchema.institutionTypeSchema)
          .addSchemas("EGroupType", SwaggerSchema.groupTypeSchema);
    };
  }
}
