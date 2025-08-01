package fr.avenirsesr.portfolio.trace.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceStatus;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public interface SwaggerSchema {
  Schema<String> traceStatusSchema =
      new StringSchema()
          .name("TraceStatus")
          ._enum(Arrays.stream(ETraceStatus.values()).map(Enum::name).toList())
          .description("Enum for trace status");
}
