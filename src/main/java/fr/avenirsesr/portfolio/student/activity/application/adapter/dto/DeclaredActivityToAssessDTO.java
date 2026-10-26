package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityAssessmentDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityPreviewDTO;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(requiredProperties = {"id", "student", "activity", "submittedAt", "status"})
public record DeclaredActivityToAssessDTO(
    UUID id,
    UserInfoDTO student,
    ActivityPreviewDTO activity,
    ActivityAssessmentDTO assessment,
    Instant submittedAt,
    Instant assessmentRequestedAt,
    Instant assessedAt,
    UserInfoDTO assessedBy,
    @Schema(ref = "#/components/schemas/EDeclaredActivityStatus") EDeclaredActivityStatus status) {}
