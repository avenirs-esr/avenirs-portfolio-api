package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProgramProgressOverviewDTO;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import java.util.Set;

public interface ProgramProgressOverviewMapper {
  static ProgramProgressOverviewDTO fromDomainToDto(
      ProgramProgress programProgress, Set<Skill> skills) {
    return new ProgramProgressOverviewDTO(
        programProgress.getId(),
        programProgress.getProgram().getName(),
        skills.stream().map(SkillOverviewMapper::fromDomainToDto).toList());
  }
}
