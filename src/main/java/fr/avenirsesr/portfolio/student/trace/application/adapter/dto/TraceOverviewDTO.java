package fr.avenirsesr.portfolio.student.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.student.trace.domain.model.enums.ETraceAuthorType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "authorType", "createdAt", "updatedAt"})
public record TraceOverviewDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/ETraceAuthorType") ETraceAuthorType authorType,
    String aiUseJustification,
    Instant createdAt,
    Instant updatedAt) {}
