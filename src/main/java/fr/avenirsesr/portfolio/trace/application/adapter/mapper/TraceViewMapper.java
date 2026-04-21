package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceViewDTO;
import fr.avenirsesr.portfolio.trace.domain.data.TraceViewData;

public interface TraceViewMapper {
  static TraceViewDTO toDTO(TraceViewData trace) {
    return new TraceViewDTO(
        trace.id(),
        trace.title(),
        trace.isAssociated(),
        trace.createdAt(),
        trace.updatedAt(),
        trace.willBeDeletedAt().orElse(null));
  }
}
