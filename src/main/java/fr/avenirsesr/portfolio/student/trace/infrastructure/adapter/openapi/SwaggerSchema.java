package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.student.trace.domain.model.enums.ETraceAuthorType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> authorTypeSchema =
      new StringSchema()
          .name("ETraceAuthorType")
          ._enum(Arrays.stream(ETraceAuthorType.values()).map(Enum::name).toList())
          .description("Enum for trace author type");
}
