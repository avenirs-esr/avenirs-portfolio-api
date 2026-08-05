package fr.avenirsesr.portfolio.student.selfknowledge.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> selfKnowledgeCategoryTypeSchema =
      new StringSchema()
          .name("ESelfKnowledgeCategory")
          ._enum(Arrays.stream(ESelfKnowledgeCategory.values()).map(Enum::name).toList())
          .description("Enum for self knowledge category types");
}
