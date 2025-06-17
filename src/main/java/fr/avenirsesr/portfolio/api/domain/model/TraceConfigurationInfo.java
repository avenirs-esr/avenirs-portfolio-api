package fr.avenirsesr.portfolio.api.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    requiredProperties = {"maxDayRemaining", "maxDayRemainingWarning", "maxDayRemainingCritical"})
public record TraceConfigurationInfo(
    int maxDayRemaining, int maxDayRemainingWarning, int maxDayRemainingCritical) {}
