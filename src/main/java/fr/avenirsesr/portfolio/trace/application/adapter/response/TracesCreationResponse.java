package fr.avenirsesr.portfolio.trace.application.adapter.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"traceId"})
public record TracesCreationResponse(UUID traceId) {}
