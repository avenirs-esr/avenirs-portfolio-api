package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProgramProgressDTO;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;

public interface ProgramProgressMapper {
  static ProgramProgressDTO fromDomainToDto(ProgramProgress programProgress) {
    return ProgramProgressDTO.builder()
        .id(programProgress.getId())
        .name(programProgress.getProgram().getName())
        .skills(programProgress.getSkills().stream().map(SkillMapper::fromDomainToDto).toList())
        .build();
  }
}
