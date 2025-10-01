package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceViewDTO;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.time.LocalDate;

public interface TraceViewMapper {
  static TraceViewDTO toDTO(Trace trace, LocalDate willBeDeletedAt) {
    return new TraceViewDTO(
        trace.getId(),
        trace.getTitle(),
        !trace.isUnassociated(),
        trace.getCreatedAt(),
        trace.getUpdatedAt(),
        willBeDeletedAt);
  }
}
