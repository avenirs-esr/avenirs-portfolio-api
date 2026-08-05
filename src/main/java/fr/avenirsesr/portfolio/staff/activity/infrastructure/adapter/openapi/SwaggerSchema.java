package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> activityThematicSchema =
      new StringSchema()
          .name("EActivityThematic")
          ._enum(Arrays.stream(EActivityThematic.values()).map(Enum::name).toList())
          .description("Enum for activity thematic");

  public static final Schema<String> activityStatusSchema =
      new StringSchema()
          .name("EActivityStatus")
          ._enum(Arrays.stream(EActivityStatus.values()).map(Enum::name).toList())
          .description("Enum for activity status");
}
