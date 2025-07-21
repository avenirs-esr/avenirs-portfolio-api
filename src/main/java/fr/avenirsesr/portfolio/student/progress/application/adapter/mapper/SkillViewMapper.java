package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillViewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import java.util.Optional;

public interface SkillViewMapper {
  static SkillViewDTO fromDomainToDto(
      SkillLevelProgress skillLevelProgress,
      int levelBySkill,
      SkillLevelProgress lastAchievedSkillLevel) {
    return new SkillViewDTO(
        skillLevelProgress.getSkillLevel().getSkill().getId(),
        skillLevelProgress.getSkillLevel().getSkill().getName(),
        skillLevelProgress.getTraces().size(),
        skillLevelProgress.getAmses().size(),
        levelBySkill,
        SkillLevelViewMapper.fromDomainToDto(skillLevelProgress),
        Optional.ofNullable(lastAchievedSkillLevel)
            .map(SkillLevelViewMapper::fromDomainToDto)
            .orElse(null));
  }
}
