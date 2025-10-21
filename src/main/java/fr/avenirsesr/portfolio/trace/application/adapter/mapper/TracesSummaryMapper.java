package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.TracesSummaryDTO;
import fr.avenirsesr.portfolio.trace.domain.data.TracesSummaryData;

public interface TracesSummaryMapper {
  static TracesSummaryDTO toDTO(TracesSummaryData tracesSummary) {
    return new TracesSummaryDTO(
        tracesSummary.associated(),
        tracesSummary.unassociated(),
        tracesSummary.totalWarnings(),
        tracesSummary.totalCriticals());
  }
}
