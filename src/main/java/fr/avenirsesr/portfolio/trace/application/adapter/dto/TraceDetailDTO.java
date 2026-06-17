package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "programName",
      "authorType",
      "aiUseJustification",
      "personalNote",
      "lockedDeclaredActivities",
      "createdAt",
      "updatedAt"
    })
public record TraceDetailDTO(
    UUID id,
    String title,
    boolean isAssociated,
    String programName,
    @Schema(ref = "#/components/schemas/ETraceAuthorType") ETraceAuthorType authorType,
    String aiUseJustification,
    String personalNote,
    String link,
    FileDTO attachment,
    Instant createdAt,
    Instant updatedAt) {}
