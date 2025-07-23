package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import java.time.LocalDate;
import java.util.Optional;

public interface SkillMapper {
  static SkillDTO fromDomainToDto(
      SkillLevelProgress skillLevelProgress, StudentProgress studentProgress) {
    var skill = skillLevelProgress.getSkillLevel().getSkill();
    int levelBySkill =
        studentProgress.getAllSkillLevels().stream()
            .filter(s -> s.getSkillLevel().getSkill().equals(skill))
            .toList()
            .size();

    SkillLevelProgress lastAchievedSkillLevel =
        studentProgress.getLastAchievedSkillLevelBySkill().get(skill).orElse(null);

    return new SkillDTO(
        skillLevelProgress.getSkillLevel().getSkill().getId(),
        skillLevelProgress.getSkillLevel().getSkill().getName(),
        skillLevelProgress.getTraces().size(),
        skillLevelProgress.getAmses().size(),
        levelBySkill,
        SkillLevelViewMapper.fromDomainToDto(skillLevelProgress),
        Optional.ofNullable(lastAchievedSkillLevel)
            .map(SkillLevelViewMapper::fromDomainToDto)
            .orElse(null),
        studentProgress.getEndDate().isBefore(LocalDate.now()));
  }
}
