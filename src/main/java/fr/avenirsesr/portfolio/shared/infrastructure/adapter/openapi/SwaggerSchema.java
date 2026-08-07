package fr.avenirsesr.portfolio.shared.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> languageSchema =
      new StringSchema()
          .name("ELanguage")
          ._enum(Arrays.stream(ELanguage.values()).map(Enum::name).toList())
          .description("Enum for language");
}
