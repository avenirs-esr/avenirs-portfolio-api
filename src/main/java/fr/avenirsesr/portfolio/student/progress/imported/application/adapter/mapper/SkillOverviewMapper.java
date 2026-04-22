package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;

public interface SkillOverviewMapper {
  static SkillOverviewDTO fromDomainToDto(SkillLevelProgress skillLevelProgress) {
    return new SkillOverviewDTO(
        skillLevelProgress.getSkillLevel().getSkill().getId(),
        skillLevelProgress.getSkillLevel().getSkill().getName(),
        SkillLevelProgressOverviewMapper.fromDomainToDto(skillLevelProgress));
  }
}
