package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> declaredActivityStatusSchema =
      new StringSchema()
          .name("EDeclaredActivityStatus")
          ._enum(Arrays.stream(EDeclaredActivityStatus.values()).map(Enum::name).toList())
          .description("Enum for declared activity status");

  public static final Schema<String> feedbackStatusSchema =
      new StringSchema()
          .name("EFeedbackStatus")
          ._enum(Arrays.stream(EFeedbackStatus.values()).map(Enum::name).toList())
          .description("Enum for feedback status");
}
