package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityAssessmentDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityPreviewDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityAssessmentType;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EActivityAssessmentStatus;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "student",
      "activity",
      "assessmentTypes",
      "submittedAt",
      "declaredActivityStatus",
      "assessmentStatus"
    })
public record DeclaredActivityToAssessDTO(
    UUID id,
    UserInfoDTO student,
    ActivityPreviewDTO activity,
    ActivityAssessmentDTO assessment,
    @ArraySchema(schema = @Schema(ref = "#/components/schemas/EActivityAssessmentType"))
        List<EActivityAssessmentType> assessmentTypes,
    Instant submittedAt,
    Instant assessmentRequestedAt,
    Instant assessedAt,
    UserInfoDTO assessedBy,
    @Schema(ref = "#/components/schemas/EDeclaredActivityStatus")
        EDeclaredActivityStatus declaredActivityStatus,
    @Schema(ref = "#/components/schemas/EActivityAssessmentStatus")
        EActivityAssessmentStatus assessmentStatus) {}
