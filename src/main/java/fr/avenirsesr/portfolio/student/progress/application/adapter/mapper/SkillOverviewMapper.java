package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;

public interface SkillOverviewMapper {
  static SkillOverviewDTO fromDomainToDto(SkillLevelProgress skillLevelProgress) {
    return new SkillOverviewDTO(
        skillLevelProgress.getSkillLevel().getSkill().getId(),
        skillLevelProgress.getSkillLevel().getSkill().getName(),
        skillLevelProgress.getTraces().size(),
        skillLevelProgress.getAmses().size(),
        SkillLevelProgressOverviewMapper.fromDomainToDto(skillLevelProgress));
  }
}
