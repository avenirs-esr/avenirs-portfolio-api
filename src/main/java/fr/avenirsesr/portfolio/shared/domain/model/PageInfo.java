package fr.avenirsesr.portfolio.shared.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"pageSize", "totalElements", "totalPages", "number"})
public record PageInfo(int pageSize, long totalElements, int totalPages, int number) {}
