package fr.avenirsesr.portfolio.authorization.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.authorization.domain.model.enums.EAuthorization;
import fr.avenirsesr.portfolio.authorization.domain.model.enums.EAuthorizationEntityType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> authorizationSchema =
      new StringSchema()
          .name("EAuthorization")
          ._enum(Arrays.stream(EAuthorization.values()).map(Enum::name).toList())
          .description("Enum for authorization");

  public static final Schema<String> authorizationEntityTypeSchema =
      new StringSchema()
          .name("EAuthorizationEntityType")
          ._enum(Arrays.stream(EAuthorizationEntityType.values()).map(Enum::name).toList())
          .description("Enum for authorization entity type");
}
