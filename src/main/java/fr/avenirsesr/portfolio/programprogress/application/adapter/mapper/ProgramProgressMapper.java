package fr.avenirsesr.portfolio.programprogress.application.adapter.mapper;

import fr.avenirsesr.portfolio.programprogress.application.adapter.dto.ProgramProgressDTO;
import fr.avenirsesr.portfolio.programprogress.domain.model.ProgramProgress;

public interface ProgramProgressMapper {
  static ProgramProgressDTO fromDomainToDto(ProgramProgress programProgress) {
    return new ProgramProgressDTO(
        programProgress.getId(),
        programProgress.getProgram().getName(),
        programProgress.getProgram().getDurationUnit(),
        programProgress.getProgram().getDurationCount());
  }
}
