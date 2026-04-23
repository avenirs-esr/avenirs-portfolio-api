package fr.avenirsesr.portfolio.file.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> fileTypeSchema =
      new StringSchema()
          .name("EFileType")
          ._enum(Arrays.stream(EFileType.values()).map(Enum::name).toList())
          .description("Enum for file type");

  public static final Schema<String> fileUserPhotoTypeSchema =
      new StringSchema()
          .name("EUserPhotoType")
          ._enum(Arrays.stream(EUserPhotoType.values()).map(Enum::name).toList())
          .description("Enum for user photo type");
}
