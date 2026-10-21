package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.student.skill.domain.model.enums.ESkillLevel;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> skillLevelSchema =
      new StringSchema()
          .name("ESkillLevel")
          ._enum(Arrays.stream(ESkillLevel.values()).map(Enum::name).toList())
          .description("Enum for skill level");
}
