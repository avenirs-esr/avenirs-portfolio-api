package fr.avenirsesr.portfolio.notification.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityUpdatableField;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ActivityUpdatedNotificationCreationData(
    UUID activityId,
    UUID studentId,
    Instant createdAt,
    List<EActivityUpdatableField> updatedFields) {}
