package fr.avenirsesr.portfolio.skill.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> skillReferentialTypeSchema =
      new StringSchema()
          .name("ESkillReferentialType")
          ._enum(Arrays.stream(ESkillReferentialType.values()).map(Enum::name).toList())
          .description("Enum for skill referential type");

  public static final Schema<String> skillKindSchema =
      new StringSchema()
          .name("ESkillKind")
          ._enum(Arrays.stream(ESkillKind.values()).map(Enum::name).toList())
          .description("Enum for skill kind");
}
