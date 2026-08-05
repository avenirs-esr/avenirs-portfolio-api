package fr.avenirsesr.portfolio.student.trace.domain.data;

public record TracesSummaryData(
    int associated, int unassociated, int totalWarnings, int totalCriticals) {}
