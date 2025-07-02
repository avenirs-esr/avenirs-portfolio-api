package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.UnassociatedTracesSummaryDTO;
import fr.avenirsesr.portfolio.trace.domain.model.UnassociatedTracesSummary;

public interface UnassociatedTracesSummaryMapper {
  static UnassociatedTracesSummaryDTO toDTO(UnassociatedTracesSummary unassociatedTracesSummary) {
    return new UnassociatedTracesSummaryDTO(
        unassociatedTracesSummary.total(),
        unassociatedTracesSummary.totalWarnings(),
        unassociatedTracesSummary.totalCriticals());
  }
}
