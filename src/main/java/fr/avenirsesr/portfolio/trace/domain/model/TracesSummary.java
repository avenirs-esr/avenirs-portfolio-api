package fr.avenirsesr.portfolio.trace.domain.model;

public record TracesSummary(
    int associated, int unassociated, int totalWarnings, int totalCriticals) {}
