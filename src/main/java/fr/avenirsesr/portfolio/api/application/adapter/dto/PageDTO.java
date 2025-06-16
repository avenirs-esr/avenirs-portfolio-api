package fr.avenirsesr.portfolio.api.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"size", "totalElements", "totalPages", "number"})
public record PageDTO(
    @Schema(description = "Number of elements per page") int size,
    @Schema(description = "Total number of elements") long totalElements,
    @Schema(description = "Total number of pages") int totalPages,
    @Schema(description = "Current page number (0-indexed)") int number) {
}
