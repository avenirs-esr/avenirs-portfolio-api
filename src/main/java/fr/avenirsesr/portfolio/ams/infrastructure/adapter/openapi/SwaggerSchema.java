package fr.avenirsesr.portfolio.ams.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public interface SwaggerSchema {
  Schema<String> amsStatusSchema =
      new StringSchema()
          .name("AmsStatus")
          ._enum(Arrays.stream(EAmsStatus.values()).map(Enum::name).toList())
          .description("Enum for ams status");
}
