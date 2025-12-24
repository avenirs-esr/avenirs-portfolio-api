package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillCategoryType;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
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
          .name("EExternalSkillType")
          ._enum(Arrays.stream(EExternalSkillType.values()).map(Enum::name).toList())
          .description("Enum for additional skill type");

  Schema<String> additionalSkillCategoryTypeSchema =
      new StringSchema()
          .name("EAdditionalSkillCategoryType")
          ._enum(Arrays.stream(EAdditionalSkillCategoryType.values()).map(Enum::name).toList())
          .description("Enum for additional skill category type");
}
