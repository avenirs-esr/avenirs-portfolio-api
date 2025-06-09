package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.TraceOverviewDTO;
import fr.avenirsesr.portfolio.api.domain.model.Trace;

public interface TraceOverviewMapper {
  static TraceOverviewDTO toDTO(Trace trace, String programName) {
    return new TraceOverviewDTO(
        trace.getId(),
        trace.getTitle(),
        trace.getSkillLevels().size(),
        trace.getAmses().size(),
        programName,
        trace.isGroup(),
        trace.getCreatedAt());
  }
}
