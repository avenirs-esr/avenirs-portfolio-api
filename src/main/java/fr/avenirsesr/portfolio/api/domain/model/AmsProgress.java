package fr.avenirsesr.portfolio.api.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"validated", "total", "percentage"})
public record AmsProgress(
    int validated,
    int total,
    int percentage) {
}
