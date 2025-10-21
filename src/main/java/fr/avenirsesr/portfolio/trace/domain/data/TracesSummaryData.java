package fr.avenirsesr.portfolio.trace.domain.data;

public record TracesSummaryData(
    int associated, int unassociated, int totalWarnings, int totalCriticals) {}
