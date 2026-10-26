package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "declaredActivityId",
      "activity",
      "student",
      "associations",
      "submittedAt",
      "status"
    })
public record DeclaredActivityAssessmentDetailsDTO(
    UUID declaredActivityId,
    ActivityContentDTO activity,
    UserInfoDTO student,
    String reflexion,
    DeclaredActivityAssociationsDTO associations,
    DeclaredActivityAssessmentDTO assessment,
    Instant submittedAt,
    @Schema(ref = "#/components/schemas/EDeclaredActivityStatus") EDeclaredActivityStatus status) {}
