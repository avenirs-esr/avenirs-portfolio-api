package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProgramProgressDTO;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;

public interface ProgramProgressMapper {
  static ProgramProgressDTO fromDomainToDto(ProgramProgress programProgress) {
    return new ProgramProgressDTO(programProgress.getId(), programProgress.getProgram().getName());
  }
}
