package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillViewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;

public interface SkillViewMapper {
  static SkillViewDTO fromDomainToDto(SkillLevelProgress skillLevelProgress, int levelBySkill) {
    return new SkillViewDTO(
        skillLevelProgress.getSkillLevel().getSkill().getId(),
        skillLevelProgress.getSkillLevel().getSkill().getName(),
        skillLevelProgress.getTraces().size(),
        skillLevelProgress.getAmses().size(),
        levelBySkill,
        SkillLevelViewMapper.fromDomainToDto(skillLevelProgress));
  }
}
