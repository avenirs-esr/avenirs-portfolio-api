package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.TraceViewDTO;
import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.enums.ETraceStatus;

public interface TraceViewMapper {
  static TraceViewDTO toDTO(Trace trace, ETraceStatus traceStatus) {
    return new TraceViewDTO(
        trace.getId(),
        trace.getTitle(),
        traceStatus,
        trace.getCreatedAt(),
        trace.getUpdatedAt(),
        trace.getDeletedAt());
  }
}
