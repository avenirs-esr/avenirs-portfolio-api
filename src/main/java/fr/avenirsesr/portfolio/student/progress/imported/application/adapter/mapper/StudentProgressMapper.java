package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.StudentProgressDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentProgressMapper {

  default StudentProgressDTO toDto(StudentProgress studentProgress) {
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
