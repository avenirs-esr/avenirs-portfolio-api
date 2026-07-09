package fr.avenirsesr.portfolio.notification.application.adapter.dto;

import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.NotificationParameters;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(requiredProperties = {"id", "createdAt", "type", "seen"})
public record NotificationDTO(
    UUID id,
    Instant createdAt,
    ENotificationType type,
    UUID elementId,
    NotificationParameters parameters,
    boolean seen) {}
