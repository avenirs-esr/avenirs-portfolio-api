package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"associationId", "trace"})
public record TraceAssociationDTO(UUID associationId, TraceOverviewDTO trace) {}
