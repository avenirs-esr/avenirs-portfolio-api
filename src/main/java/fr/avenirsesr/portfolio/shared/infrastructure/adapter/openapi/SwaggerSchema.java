package fr.avenirsesr.portfolio.shared.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.common.openapi.infrastructure.adapter.BaseSwaggerSchema;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public class SwaggerSchema extends BaseSwaggerSchema {
  public static Schema<String> portfolioTypeSchema =
      new StringSchema()
          .name("EPortfolioType")
          ._enum(Arrays.stream(EPortfolioType.values()).map(Enum::name).toList())
          .description("Enum for portfolio types");
}
