package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategoryType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> selfKnowledgeCategoryTypeSchema =
      new StringSchema()
          .name("ESelfKnowledgeCategoryType")
          ._enum(Arrays.stream(ESelfKnowledgeCategoryType.values()).map(Enum::name).toList())
          .description("Enum for self knowledge category types");
}
