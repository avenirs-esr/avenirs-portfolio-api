package fr.avenirsesr.portfolio.trace.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public interface SwaggerSchema {
  Schema<String> traceStatusSchema =
      new StringSchema()
          .name("ETraceStatus")
          ._enum(Arrays.stream(ETraceStatus.values()).map(Enum::name).toList())
          .description("Enum for trace status");
}
