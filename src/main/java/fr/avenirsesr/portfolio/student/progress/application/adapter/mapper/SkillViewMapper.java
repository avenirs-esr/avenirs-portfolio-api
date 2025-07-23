package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillViewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import java.util.Optional;

public interface SkillViewMapper {
  static SkillViewDTO fromDomainToDto(
      SkillLevelProgress skillLevelProgress, StudentProgress studentProgress) {
    var skill = skillLevelProgress.getSkillLevel().getSkill();
    int levelBySkill =
        studentProgress.getAllSkillLevels().stream()
            .filter(s -> s.getSkillLevel().getSkill().equals(skill))
            .toList()
            .size();

    SkillLevelProgress lastAchievedSkillLevel =
        studentProgress.getLastAchievedSkillLevelBySkill().get(skill).orElse(null);

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
