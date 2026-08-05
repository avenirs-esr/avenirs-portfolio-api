package fr.avenirsesr.portfolio.student.program.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.student.program.domain.model.enums.EProgramStatus;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> programStatusSchema =
      new StringSchema()
          .name("EProgramStatus")
          ._enum(Arrays.stream(EProgramStatus.values()).map(Enum::name).toList())
          .description("Enum for program status");
}
