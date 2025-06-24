package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.UnassociatedTracesSummaryDTO;
import fr.avenirsesr.portfolio.api.domain.model.UnassociatedTracesSummary;

public interface UnassociatedTracesSummaryMapper {
  static UnassociatedTracesSummaryDTO toDTO(UnassociatedTracesSummary unassociatedTracesSummary) {
    return new UnassociatedTracesSummaryDTO(
        unassociatedTracesSummary.total(),
        unassociatedTracesSummary.totalWarnings(),
        unassociatedTracesSummary.totalCriticals());
  }
}
