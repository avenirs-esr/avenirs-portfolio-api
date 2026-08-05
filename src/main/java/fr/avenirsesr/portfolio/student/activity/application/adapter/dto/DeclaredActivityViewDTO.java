package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "activityId",
      "title",
      "thematic",
      "summary",
      "description",
      "status"
    })
public record DeclaredActivityViewDTO(
    UUID id,
    UUID activityId,
    String title,
    @Schema(ref = "#/components/schemas/EActivityThematic") EActivityThematic thematic,
    String summary,
    String description,
    LocalDate optionalStartDate,
    LocalDate optionalEndDate,
    @Schema(ref = "#/components/schemas/EDeclaredActivityStatus") EDeclaredActivityStatus status,
    LocalDate startDate,
    LocalDate endDate,
    boolean valorized,
    Instant updatedAt) {}
