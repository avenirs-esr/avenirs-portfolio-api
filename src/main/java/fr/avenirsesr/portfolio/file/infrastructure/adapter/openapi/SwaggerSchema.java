package fr.avenirsesr.portfolio.file.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.file.domain.model.EFileCategory;
import fr.avenirsesr.portfolio.file.domain.model.EFileType;
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

  public static final Schema<String> fileCategorySchema =
      new StringSchema()
          .name("EFileCategory")
          ._enum(Arrays.stream(EFileCategory.values()).map(Enum::name).toList())
          .description("Enum for file category");
}
