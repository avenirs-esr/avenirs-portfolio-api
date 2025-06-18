package fr.avenirsesr.portfolio.api.application.adapter.dto;

import fr.avenirsesr.portfolio.api.domain.model.enums.ETraceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "status", "createdAt", "updatedAt", "deletionDate"})
public record TraceViewDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/TraceStatus") ETraceStatus status,
    Instant createdAt,
    Instant updatedAt,
    Instant deletionDate) {}
