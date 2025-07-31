package fr.avenirsesr.portfolio.configuration.domain.model;

public record TraceConfigurationInfo(
    int maxDayRemaining, int maxDayRemainingWarning, int maxDayRemainingCritical) {}
