package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProgramProgressDTO;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import java.util.Set;

public interface ProgramProgressMapper {
  static ProgramProgressDTO fromDomainToDto(ProgramProgress programProgress, Set<Skill> skills) {
    return new ProgramProgressDTO(
        programProgress.getId(),
        programProgress.getProgram().getName(),
        skills.stream().map(SkillMapper::fromDomainToDto).toList());
  }
}
