package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record FeedbackOverviewDTO(
    UUID id,
    UserInfoDTO staff,
    UserInfoDTO student,
    String feedback,
    @Schema(ref = "#/components/schemas/EFeedbackStatus") EFeedbackStatus status,
    Instant createdAt,
    Instant updatedAt) {}
