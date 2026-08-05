package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import java.util.UUID;

public record FeedbackCreationData(
    UUID declaredActivityId, UUID studentId, EFeedbackStatus targetStatus, String feedbackText) {}
