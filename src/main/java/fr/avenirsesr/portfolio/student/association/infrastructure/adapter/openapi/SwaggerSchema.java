package fr.avenirsesr.portfolio.student.association.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> associationContextTypeSchema =
      new StringSchema()
          .name("EAssociationContextType")
          ._enum(Arrays.stream(EAssociationContextType.values()).map(Enum::name).toList())
          .description("Enum for association context type");
}
