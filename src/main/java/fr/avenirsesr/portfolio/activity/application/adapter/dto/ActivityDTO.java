package fr.avenirsesr.portfolio.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "thematic",
      "summary",
      "description",
      "executionPeriodInfo",
      "createdAt",
      "updatedAt"
    })
public record ActivityDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/EActivityThematic") EActivityThematic thematic,
    String summary,
    String description,
    String executionPeriodInfo,
    Instant createdAt,
    Instant updatedAt) {}
