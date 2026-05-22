package fr.avenirsesr.portfolio.user.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.model.enums.EContextType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> userContextSchema =
      new StringSchema()
          .name("EContextType")
          ._enum(Arrays.stream(EContextType.values()).map(Enum::name).toList())
          .description("Enum for context type");

  public static final Schema<String> userCategorySchema =
      new StringSchema()
          .name("EUserCategory")
          ._enum(Arrays.stream(EUserCategory.values()).map(Enum::name).toList())
          .description("Enum for user category");
}
