package fr.avenirsesr.portfolio.trace.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"total", "totalWarnings", "totalCriticals"})
public record UnassociatedTracesSummary(int total, int totalWarnings, int totalCriticals) {}
