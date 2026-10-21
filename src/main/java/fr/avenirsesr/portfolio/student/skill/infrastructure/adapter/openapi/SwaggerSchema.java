package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.student.skill.domain.model.enums.EDeclaredSkillLevel;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> declaredSkillLevelSchema =
      new StringSchema()
          .name("EDeclaredSkillLevel")
          ._enum(Arrays.stream(EDeclaredSkillLevel.values()).map(Enum::name).toList())
          .description("Enum for declared skill level");
}
