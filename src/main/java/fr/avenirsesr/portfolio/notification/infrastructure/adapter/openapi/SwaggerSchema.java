package fr.avenirsesr.portfolio.notification.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> notificationTypeSchema =
      new StringSchema()
          .name("ENotificationType")
          ._enum(Arrays.stream(ENotificationType.values()).map(Enum::name).toList())
          .description("Enum for notification type");
}
