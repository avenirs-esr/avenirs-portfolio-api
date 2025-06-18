package fr.avenirsesr.portfolio.api.infrastructure.configuration;

import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.api.domain.model.enums.ETraceStatus;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiEnumConfiguration {
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .components(
            new Components()
                .addSchemas(
                    "SkillLevelStatus",
                    new StringSchema()
                        .name("SkillLevelStatus")
                        ._enum(Arrays.stream(ESkillLevelStatus.values()).map(Enum::name).toList())
                        .description("Enum for skill level status"))
                .addSchemas(
                    "TraceStatus",
                    new StringSchema()
                        .name("TraceStatus")
                        ._enum(Arrays.stream(ETraceStatus.values()).map(Enum::name).toList())
                        .description("Enum for trace status")));
  }
}
