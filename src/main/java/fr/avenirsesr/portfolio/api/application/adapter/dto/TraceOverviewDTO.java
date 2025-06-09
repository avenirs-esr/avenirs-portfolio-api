package fr.avenirsesr.portfolio.api.application.adapter.dto;

import java.time.Instant;
import java.util.UUID;

public record TraceOverviewDTO(
    UUID traceId,
    String title,
    Integer skillCount,
    Integer AMSCount,
    String programName,
    boolean isGroup,
    Instant createdAt) {}
