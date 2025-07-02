package fr.avenirsesr.portfolio.programprogress.application.adapter.mapper;

import fr.avenirsesr.portfolio.programprogress.application.adapter.dto.ProgramProgressOverviewDTO;
import fr.avenirsesr.portfolio.programprogress.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.programprogress.domain.model.Skill;
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
