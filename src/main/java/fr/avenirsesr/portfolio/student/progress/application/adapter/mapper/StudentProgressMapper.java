package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.StudentProgressDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;

public interface StudentProgressMapper {
  static StudentProgressDTO toDto(StudentProgress studentProgress) {
    return new StudentProgressDTO(
        studentProgress.getId(),
        studentProgress.getStudent().getId(),
        new StudentProgressDTO.TrainingPathDTO(
            studentProgress.getTrainingPath().getId(),
            studentProgress.getTrainingPath().getProgram().getName(),
            studentProgress.getTrainingPath().getProgram().getDurationUnit().orElse(null),
            studentProgress.getTrainingPath().getProgram().getDurationCount().orElse(null)));
  }
}
