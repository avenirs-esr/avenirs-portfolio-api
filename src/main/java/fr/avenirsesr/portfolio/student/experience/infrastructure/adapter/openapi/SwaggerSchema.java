package fr.avenirsesr.portfolio.student.experience.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.student.experience.domain.model.enums.EExperienceType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> experienceTypeSchema =
      new StringSchema()
          .name("EExperienceType")
          ._enum(Arrays.stream(EExperienceType.values()).map(Enum::name).toList())
          .description("Enum for trace status");
}
