package fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public interface SwaggerSchema {
  Schema<String> programStatusSchema =
      new StringSchema()
          .name("EProgramStatus")
          ._enum(Arrays.stream(EProgramStatus.values()).map(Enum::name).toList())
          .description("Enum for program status");
}
