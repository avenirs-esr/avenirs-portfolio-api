package fr.avenirsesr.portfolio.staff.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
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
    String recommendedCompletionContexts,
    LocalDate startDate,
    LocalDate endDate,
    boolean enableReflection,
    int traceAllowedAssociations,
    int feedbackAllowedIterations,
    Boolean hasEnrolledStudent,
    List<FileDTO> files,
    List<String> links,
    Instant createdAt,
    Instant updatedAt) {}
