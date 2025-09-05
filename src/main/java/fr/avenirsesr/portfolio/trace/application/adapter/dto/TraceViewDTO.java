package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "status", "createdAt", "updatedAt", "willBeDeletedAt"})
public record TraceViewDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/ETraceStatus") ETraceStatus status,
    Instant createdAt,
    Instant updatedAt,
    LocalDate willBeDeletedAt) {}
