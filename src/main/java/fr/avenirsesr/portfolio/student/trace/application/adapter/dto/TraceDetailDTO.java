package fr.avenirsesr.portfolio.student.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.student.trace.domain.model.enums.ETraceAuthorType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "authorType",
      "aiUseJustification",
      "personalNote",
      "lockedDeclaredActivities",
      "valorized",
      "createdAt",
      "updatedAt"
    })
public record TraceDetailDTO(
    UUID id,
    String title,
    boolean isAssociated,
    @Schema(ref = "#/components/schemas/ETraceAuthorType") ETraceAuthorType authorType,
    String aiUseJustification,
    String personalNote,
    String link,
    FileDTO attachment,
    boolean valorized,
    Instant createdAt,
    Instant updatedAt) {}
