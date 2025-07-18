package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillViewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;

public interface SkillViewMapper {
  static SkillViewDTO fromDomainToDto(SkillLevelProgress currentSkillLevel, int levelBySkill) {
    return new SkillViewDTO(
        currentSkillLevel.getSkillLevel().getSkill().getId(),
        currentSkillLevel.getSkillLevel().getSkill().getName(),
        currentSkillLevel.getTraces().size(),
        currentSkillLevel.getAmses().size(),
        levelBySkill,
        SkillLevelViewMapper.fromDomainToDto(currentSkillLevel));
  }
}
