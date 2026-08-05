package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.DeclaredSkillProgressDTO;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceDetailDTO;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "declaredActivityId",
      "activity",
      "student",
      "status",
      "createdAt",
      "updatedAt"
    })
public record FeedbackDetailsDTO(
    UUID id,
    UUID declaredActivityId,
    ActivityContentDTO activity,
    UserInfoDTO student,
    String reflexion,
    String feedback,
    @Schema(ref = "#/components/schemas/EFeedbackStatus") EFeedbackStatus status,
    List<TraceDetailDTO> associatedTraces,
    List<DeclaredSkillProgressDTO> associatedDeclaredSkills,
    Instant createdAt,
    Instant updatedAt) {}
