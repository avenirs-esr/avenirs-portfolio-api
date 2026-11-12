package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.SubActivityOverviewDTO;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "activity",
      "subActivities",
      "valorized",
      "status",
      "createdAt",
      "updatedAt"
    })
public record DeclaredActivityDetailsDTO(
    UUID id,
    ActivityContentDTO activity,
    List<SubActivityOverviewDTO> subActivities,
    @Schema(ref = "#/components/schemas/EDeclaredActivityStatus") EDeclaredActivityStatus status,
    String reflection,
    LocalDate startDate,
    LocalDate endDate,
    Instant finishedAt,
    boolean valorized,
    Instant createdAt,
    Instant updatedAt,
    List<FeedbackOverviewDTO> feedbacks,
    ReceivedAssessmentDTO assessment) {}
