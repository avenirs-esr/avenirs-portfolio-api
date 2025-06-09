package fr.avenirsesr.portfolio.api.application.adapter.dto;

import java.util.UUID;

public record SkillOverviewDTO(
    UUID id,
    String name,
    int traceCount,
    int activityCount,
    SkillLevelOverviewDTO currentSkillLevel) {}
