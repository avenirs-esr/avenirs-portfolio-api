package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.TracesSummaryDTO;
import fr.avenirsesr.portfolio.trace.domain.model.TracesSummary;

public interface TracesSummaryMapper {
  static TracesSummaryDTO toDTO(TracesSummary tracesSummary) {
    return new TracesSummaryDTO(
        tracesSummary.associated(),
        tracesSummary.unassociated(),
        tracesSummary.totalWarnings(),
        tracesSummary.totalCriticals());
  }
}
