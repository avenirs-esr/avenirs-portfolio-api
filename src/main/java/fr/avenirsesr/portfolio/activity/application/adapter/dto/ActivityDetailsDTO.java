package fr.avenirsesr.portfolio.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "thematic",
      "isSubscribed",
      "banner",
      "summary",
      "executionPeriodInfo"
    })
public record ActivityDetailsDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/EActivityThematic") EActivityThematic thematic,
    boolean isSubscribed,
    FileDTO banner,
    String summary,
    String executionPeriodInfo,
    Instant createdAt,
    Instant updatedAt) {}
