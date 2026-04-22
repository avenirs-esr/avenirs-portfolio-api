package fr.avenirsesr.portfolio.association.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.association.domain.model.EAssociationContextType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public interface SwaggerSchema {
  Schema<String> associationContextTypeSchema =
      new StringSchema()
          .name("EAssociationContextType")
          ._enum(Arrays.stream(EAssociationContextType.values()).map(Enum::name).toList())
          .description("Enum for association context type");
}
