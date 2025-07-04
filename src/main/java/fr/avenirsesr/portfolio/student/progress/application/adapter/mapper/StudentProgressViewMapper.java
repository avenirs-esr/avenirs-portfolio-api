package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.StudentProgressViewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.TrainingPath;
import java.util.Set;

public interface StudentProgressViewMapper {
  static StudentProgressViewDTO fromDomainToDto(
      TrainingPath trainingPath, Set<StudentProgress> studentProgresses) {
    return new StudentProgressViewDTO(
        trainingPath.getId(),
        trainingPath.getProgram().getName(),
        studentProgresses.stream()
            .map(
                studentProgress ->
                    SkillViewMapper.fromDomainToDto(
                        studentProgress,
                        trainingPath.getSkillLevels().stream()
                            .filter(s -> s.getId().equals(studentProgress.getSkillLevel().getId()))
                            .mapToInt(sl -> sl.getSkill().getSkillLevels().size())
                            .findFirst()
                            .orElse(0)))
            .toList());
  }
}
