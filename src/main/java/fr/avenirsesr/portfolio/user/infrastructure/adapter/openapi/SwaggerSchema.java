package fr.avenirsesr.portfolio.user.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public interface SwaggerSchema {
  Schema<String> userCategorySchema =
      new StringSchema()
          .name("UserCategory")
          ._enum(Arrays.stream(EUserCategory.values()).map(Enum::name).toList())
          .description("Enum for user category");

  Schema<String> userPhotoTypeSchema =
      new StringSchema()
          .name("UserPhotoType")
          ._enum(Arrays.stream(EUserPhotoType.values()).map(Enum::name).toList())
          .description("Enum for user photo type");
}
