package fr.avenirsesr.portfolio.api.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "traceId",
      "title",
      "skillCount",
      "AMSCount",
      "programName",
      "isGroup",
      "createdAt"
    })
public record TraceOverviewDTO(
    UUID traceId,
    String title,
    Integer skillCount,
    Integer AMSCount,
    String programName,
    boolean isGroup,
    Instant createdAt) {}
