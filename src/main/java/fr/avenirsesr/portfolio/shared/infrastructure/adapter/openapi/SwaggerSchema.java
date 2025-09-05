package fr.avenirsesr.portfolio.shared.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.shared.domain.model.enums.EDurationUnit;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortOrder;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public interface SwaggerSchema {
  Schema<String> sharedDurationUnitSchema =
      new StringSchema()
          .name("EDurationUnit")
          ._enum(Arrays.stream(EDurationUnit.values()).map(Enum::name).toList())
          .description("Enum for duration units");

  Schema<String> sharedErrorCodeSchema =
      new StringSchema()
          .name("EErrorCode")
          ._enum(Arrays.stream(EErrorCode.values()).map(Enum::name).toList())
          .description("Enum for error codes");

  Schema<String> sharedLanguageSchema =
      new StringSchema()
          .name("ELanguage")
          ._enum(Arrays.stream(ELanguage.values()).map(Enum::name).toList())
          .description("Enum for languages");

  Schema<String> sharedPortfolioTypeSchema =
      new StringSchema()
          .name("EPortfolioType")
          ._enum(Arrays.stream(EPortfolioType.values()).map(Enum::name).toList())
          .description("Enum for portfolio types");

  Schema<String> sharedSortFieldSchema =
      new StringSchema()
          .name("ESortField")
          ._enum(Arrays.stream(ESortField.values()).map(Enum::name).toList())
          .description("Enum for sort fields");

  Schema<String> sharedSortOrderSchema =
      new StringSchema()
          .name("ESortOrder")
          ._enum(Arrays.stream(ESortOrder.values()).map(Enum::name).toList())
          .description("Enum for sort orders");
}
