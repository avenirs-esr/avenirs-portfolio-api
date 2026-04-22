package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceOverviewDTO;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;

public interface TraceOverviewMapper {
  static TraceOverviewDTO toDTO(Trace trace, String programName) {
    return new TraceOverviewDTO(
        trace.getId(),
        trace.getTitle(),
        programName,
        trace.isGroup(),
        trace.getCreatedAt(),
        trace.getUpdatedAt());
  }
}
