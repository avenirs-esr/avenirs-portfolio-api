package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillDTO;
import fr.avenirsesr.portfolio.student.progress.domain.dto.SkillLevelProgressWithTraceCountDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import java.time.LocalDate;
import java.util.Optional;

public interface SkillMapper {
  static SkillDTO fromDomainToDto(
      SkillLevelProgressWithTraceCountDTO dto, StudentProgress studentProgress) {
    var skill = dto.skillLevelProgress().getSkillLevel().getSkill();
    int levelBySkill =
        studentProgress.getAllSkillLevels().stream()
            .filter(s -> s.getSkillLevel().getSkill().equals(skill))
            .toList()
            .size();

    SkillLevelProgress lastAchievedSkillLevel =
        studentProgress.getLastAchievedSkillLevelBySkill().get(skill).orElse(null);

    return new SkillDTO(
        skill.getId(),
        skill.getName(),
        levelBySkill,
        SkillLevelViewMapper.fromDomainToDto(dto),
        Optional.ofNullable(lastAchievedSkillLevel)
            .map(
                skillLevelProgress ->
                    SkillLevelViewMapper.fromDomainToDto(
                        new SkillLevelProgressWithTraceCountDTO(skillLevelProgress, 0)))
            .orElse(null),
        studentProgress.getEndDate().isBefore(LocalDate.now()));
  }
}
