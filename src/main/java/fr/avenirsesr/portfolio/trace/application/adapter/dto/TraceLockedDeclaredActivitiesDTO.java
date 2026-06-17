package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "traceId",
      "traceTitle",
      "lockedDeclaredActivities",
    })
public record TraceLockedDeclaredActivitiesDTO(
    UUID traceId, String traceTitle, List<TraceDeclaredActivityDTO> lockedDeclaredActivities) {}
