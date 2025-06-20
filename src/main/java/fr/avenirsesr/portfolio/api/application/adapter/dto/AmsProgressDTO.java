package fr.avenirsesr.portfolio.api.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"startedActivities", "totalActivities"})
public record AmsProgress(int startedActivities, int totalActivities) {}
