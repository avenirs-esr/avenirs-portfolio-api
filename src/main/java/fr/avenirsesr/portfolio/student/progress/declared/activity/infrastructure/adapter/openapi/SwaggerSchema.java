package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public interface SwaggerSchema {
  Schema<String> declaredActivityStatusSchema =
      new StringSchema()
          .name("EDeclaredActivityStatus")
          ._enum(Arrays.stream(EDeclaredActivityStatus.values()).map(Enum::name).toList())
          .description("Enum for declared activity status");
}
