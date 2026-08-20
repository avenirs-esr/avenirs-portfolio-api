package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"id", "staff", "student", "status", "createdAt", "updatedAt"})
public record FeedbackOverviewDTO(
    UUID id,
    UserInfoDTO staff,
    UserInfoDTO student,
    String feedback,
    @Schema(ref = "#/components/schemas/EFeedbackStatus") EFeedbackStatus status,
    List<FileDTO> attachments,
    Instant createdAt,
    Instant updatedAt) {}
