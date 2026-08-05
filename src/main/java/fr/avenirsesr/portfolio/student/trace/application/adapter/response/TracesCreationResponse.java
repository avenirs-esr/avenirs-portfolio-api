package fr.avenirsesr.portfolio.student.trace.application.adapter.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id"})
public record TracesCreationResponse(UUID traceId) {}
