package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceViewDTO;
import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;

public interface TraceViewMapper {
  static TraceViewDTO toDTO(Trace trace) {
    return new TraceViewDTO(
        trace.getId(),
        trace.getTitle(),
        trace.isUnassociated() ? ETraceStatus.UNASSOCIATED : ETraceStatus.ASSOCIATED,
        trace.getCreatedAt(),
        trace.getUpdatedAt(),
        trace.getDeletedAt().orElse(null));
  }
}
