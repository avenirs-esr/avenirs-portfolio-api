package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "traceId",
      "title",
      "programName",
      "authorType",
      "createdAt",
      "updatedAt"
    })
public record TraceOverviewDTO(
    UUID traceId,
    String title,
    String programName,
    @Schema(ref = "#/components/schemas/ETraceAuthorType") ETraceAuthorType authorType,
    Instant createdAt,
    Instant updatedAt) {}
