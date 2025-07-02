package fr.avenirsesr.portfolio.configuration.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    requiredProperties = {"maxDayRemaining", "maxDayRemainingWarning", "maxDayRemainingCritical"})
public record TraceConfigurationInfo(
    int maxDayRemaining, int maxDayRemainingWarning, int maxDayRemainingCritical) {}
