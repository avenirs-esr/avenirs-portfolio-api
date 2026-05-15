package fr.avenirsesr.portfolio.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "activityId",
      "title",
      "author",
      "thematic",
      "activityStatus",
      "updatedAt"
    })
public record ActivityStaffOverviewDTO(
    UUID activityId,
    AuthorDTO author,
    String title,
    @Schema(ref = "#/components/schemas/EActivityThematic") EActivityThematic thematic,
    @Schema(ref = "#/components/schemas/EActivityStatus") EActivityStatus activityStatus,
    Instant updatedAt) {}
