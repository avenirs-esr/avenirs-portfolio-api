package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.StudentProgressViewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;

public interface StudentProgressViewMapper {
  static StudentProgressViewDTO fromDomainToDto(StudentProgress studentProgress) {
    return new StudentProgressViewDTO(
        studentProgress.getId(),
        studentProgress.getTrainingPath().getProgram().getName(),
        studentProgress.getCurrentSkillLevels().stream()
            .map(
                skillLevelProgress -> {
                  var skill = skillLevelProgress.getSkillLevel().getSkill();
                  return SkillViewMapper.fromDomainToDto(
                      skillLevelProgress,
                      studentProgress.getAllSkillLevels().stream()
                          .filter(s -> s.getSkillLevel().getSkill().equals(skill))
                          .toList()
                          .size(),
                      studentProgress.getLastAchievedSkillLevelBySkill().get(skill).orElse(null));
                })
            .toList());
  }
}
