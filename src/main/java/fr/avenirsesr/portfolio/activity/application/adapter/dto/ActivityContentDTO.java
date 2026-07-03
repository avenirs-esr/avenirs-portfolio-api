package fr.avenirsesr.portfolio.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "thematic",
      "summary",
      "enableReflection",
      "traceAllowedAssociations",
      "feedbackAllowedIterations",
      "createdAt",
      "updatedAt"
    })
public record ActivityContentDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/EActivityThematic") EActivityThematic thematic,
    String summary,
    String description,
    String executionPeriodInfo,
    boolean enableReflection,
    int traceAllowedAssociations,
    int feedbackAllowedIterations,
    Boolean hasEnrolledStudent,
    List<FileDTO> files,
    List<String> links,
    Instant createdAt,
    Instant updatedAt) {}
