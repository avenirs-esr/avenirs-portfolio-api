package fr.avenirsesr.portfolio.api.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"total", "totalWarnings", "totalCriticals"})
public record UnassociatedTracesSummaryDTO(int total, int totalWarnings, int totalCriticals) {}
