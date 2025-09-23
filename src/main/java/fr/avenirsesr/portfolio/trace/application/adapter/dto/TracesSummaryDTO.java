package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"associated", "unassociated", "totalWarnings", "totalCriticals"})
public record TracesSummaryDTO(
    int associated, int unassociated, int totalWarnings, int totalCriticals) {}
