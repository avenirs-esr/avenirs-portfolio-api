package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProgramProgressViewDTO;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import java.util.Set;

public interface ProgramProgressViewMapper {
  static ProgramProgressViewDTO fromDomainToDto(
      ProgramProgress programProgress, Set<Skill> skills) {
    return new ProgramProgressViewDTO(
        programProgress.getId(),
        programProgress.getProgram().getName(),
        skills.stream()
            .map(
                skill ->
                    SkillViewMapper.fromDomainToDto(
                        skill,
                        programProgress.getSkills().stream()
                            .filter(s -> s.getId().equals(skill.getId()))
                            .mapToInt(sk -> sk.getSkillLevels().size())
                            .findFirst()
                            .orElse(0)))
            .toList());
  }
}
