package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceViewDTO;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceStatus;

public interface TraceViewMapper {
  static TraceViewDTO toDTO(Trace trace, ETraceStatus traceStatus) {
    return new TraceViewDTO(
        trace.getId(),
        trace.getTitle(),
        traceStatus,
        trace.getCreatedAt(),
        trace.getUpdatedAt(),
        trace.getDeletedAt().orElse(null));
  }
}
