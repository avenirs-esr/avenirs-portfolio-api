package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillViewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;

public interface SkillViewMapper {
  static SkillViewDTO fromDomainToDto(StudentProgress studentProgress, int levelCount) {
    return new SkillViewDTO(
        studentProgress.getSkillLevel().getSkill().getId(),
        studentProgress.getSkillLevel().getSkill().getName(),
        studentProgress.getSkillLevel().getTraces().size(),
        studentProgress.getSkillLevel().getAmses().size(),
        levelCount,
        SkillLevelViewMapper.fromDomainToDto(studentProgress));
  }
}
