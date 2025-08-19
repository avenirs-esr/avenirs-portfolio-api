package fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.model;

public record TraceConfiguration(
    int maxRemainingDays, int maxRemainingDaysBeforeWarning, int maxRemainingDaysBeforeCritical) {}
