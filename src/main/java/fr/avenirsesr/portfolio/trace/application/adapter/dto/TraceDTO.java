package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "status", "createdAt", "updatedAt", "deletedAt"})
public record TraceDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/TraceStatus") ETraceStatus status,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt) {}
