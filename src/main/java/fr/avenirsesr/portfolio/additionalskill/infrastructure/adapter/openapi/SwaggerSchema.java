package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public interface SwaggerSchema {
  Schema<String> additionalSkillLevelSchema =
      new StringSchema()
          .name("EAdditionalSkillLevel")
          ._enum(Arrays.stream(EAdditionalSkillLevel.values()).map(Enum::name).toList())
          .description("Enum for additional skill level");

  Schema<String> additionalSkillTypeSchema =
      new StringSchema()
          .name("EAdditionalSkillType")
          ._enum(Arrays.stream(EAdditionalSkillType.values()).map(Enum::name).toList())
          .description("Enum for additional skill type");
}
