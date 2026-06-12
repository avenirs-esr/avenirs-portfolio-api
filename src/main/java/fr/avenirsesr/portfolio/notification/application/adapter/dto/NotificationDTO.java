package fr.avenirsesr.portfolio.notification.application.adapter.dto;

import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record NotificationDTO(
    UUID id,
    Instant createdAt,
    ENotificationType type,
    UUID elementId,
    List<String> parameters,
    boolean seen) {}
