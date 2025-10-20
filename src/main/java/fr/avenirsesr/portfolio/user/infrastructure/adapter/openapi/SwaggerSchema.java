package fr.avenirsesr.portfolio.user.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.model.enums.EContextType;
import fr.avenirsesr.portfolio.user.domain.model.enums.EExternalSource;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public interface SwaggerSchema {
  Schema<String> userContextSchema =
      new StringSchema()
          .name("EContextType")
          ._enum(Arrays.stream(EContextType.values()).map(Enum::name).toList())
          .description("Enum for context type");

  Schema<String> userExternalSourceSchema =
      new StringSchema()
          .name("EExternalSource")
          ._enum(Arrays.stream(EExternalSource.values()).map(Enum::name).toList())
          .description("Enum for external source");

  Schema<String> userCategorySchema =
      new StringSchema()
          .name("EUserCategory")
          ._enum(Arrays.stream(EUserCategory.values()).map(Enum::name).toList())
          .description("Enum for user category");
}
