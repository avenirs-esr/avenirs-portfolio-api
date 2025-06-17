package fr.avenirsesr.portfolio.api.application.adapter.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"code", "message"})
public record ErrorResponse(String code, String message) {}
