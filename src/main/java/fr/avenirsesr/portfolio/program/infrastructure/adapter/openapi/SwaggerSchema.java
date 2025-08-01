package fr.avenirsesr.portfolio.program.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public interface SwaggerSchema {
  Schema<String> skillLevelStatusSchema =
      new StringSchema()
          .name("SkillLevelStatus")
          ._enum(Arrays.stream(ESkillLevelStatus.values()).map(Enum::name).toList())
          .description("Enum for skill level status");
}
