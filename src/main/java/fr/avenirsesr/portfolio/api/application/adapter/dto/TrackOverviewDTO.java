package fr.avenirsesr.portfolio.api.application.adapter.dto;

import java.time.Instant;
import java.util.UUID;

public record TrackOverviewDTO(
    UUID trackId,
    String title,
    Integer skillCount,
    Integer AMSCount,
    String programName,
    boolean isGroup,
    Instant createdAt) {}
