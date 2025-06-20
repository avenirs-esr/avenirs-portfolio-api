package fr.avenirsesr.portfolio.api.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"size", "totalElements", "totalPages", "number"})
public record PageInfo(int size, long totalElements, int totalPages, int number) {}
