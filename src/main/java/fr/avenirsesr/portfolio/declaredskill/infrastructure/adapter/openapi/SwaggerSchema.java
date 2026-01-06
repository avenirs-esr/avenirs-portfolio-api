package fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public interface SwaggerSchema {
  Schema<String> declaredSkillLevelSchema =
      new StringSchema()
          .name("EDeclaredSkillLevel")
          ._enum(Arrays.stream(EDeclaredSkillLevel.values()).map(Enum::name).toList())
          .description("Enum for declared skill level");

  Schema<String> externalSkillTypeSchema =
      new StringSchema()
          .name("EExternalSkillType")
          ._enum(Arrays.stream(EExternalSkillType.values()).map(Enum::name).toList())
          .description("Enum for external skill type");

  Schema<String> externalSkillCategoryTypeSchema =
      new StringSchema()
          .name("EExternalSkillCategoryType")
          ._enum(Arrays.stream(EExternalSkillCategoryType.values()).map(Enum::name).toList())
          .description("Enum for external skill category type");
}
