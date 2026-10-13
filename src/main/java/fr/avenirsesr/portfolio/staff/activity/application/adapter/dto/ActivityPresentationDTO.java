package fr.avenirsesr.portfolio.staff.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "thematic",
      "activityStatus",
      "banner",
      "summary",
    })
public record ActivityPresentationDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/EActivityThematic") EActivityThematic thematic,
    @Schema(ref = "#/components/schemas/EActivityStatus") EActivityStatus activityStatus,
    UUID subscribedDeclaredActivity,
    @Schema(ref = "#/components/schemas/EDeclaredActivityStatus")
        EDeclaredActivityStatus subscribedDeclaredActivityStatus,
    FileDTO banner,
    String summary,
    String description,
    String recommendedCompletionContexts,
    Instant createdAt,
    Instant updatedAt) {}
