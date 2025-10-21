package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.StudentProgressViewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillLevelProgressWithTraceCountData;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import java.util.List;

public interface StudentProgressViewMapper {
  static StudentProgressViewDTO fromDomainToDto(
      StudentProgress studentProgress, List<SkillLevelProgressWithTraceCountData> skillLevels) {
    return new StudentProgressViewDTO(
        studentProgress.getId(),
        studentProgress.getTrainingPath().getProgram().getName(),
        skillLevels.stream()
            .map(
                skillLevelProgress ->
                    SkillMapper.fromDomainToDto(skillLevelProgress, studentProgress))
            .toList());
  }
}
