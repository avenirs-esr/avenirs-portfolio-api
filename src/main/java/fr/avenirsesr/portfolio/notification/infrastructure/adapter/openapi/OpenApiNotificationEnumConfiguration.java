package fr.avenirsesr.portfolio.notification.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiNotificationEnumConfiguration {
  @Bean
  public OpenApiCustomizer notificationEnumCustomizer() {
    return openApi ->
        openApi
            .getComponents()
            .addSchemas("ENotificationType", SwaggerSchema.notificationTypeSchema);
  }
}
