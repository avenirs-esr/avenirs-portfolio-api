package fr.avenirsesr.portfolio.api.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(requiredProperties = {"id", "name", "traceCount", "activityCount", "currentSkillLevel"})
public record SkillOverviewDTO(
    UUID id,
    String name,
    int traceCount,
    int activityCount,
    SkillLevelOverviewDTO currentSkillLevel) {}
