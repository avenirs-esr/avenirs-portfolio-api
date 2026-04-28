package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.StudentProgressViewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StudentProgressViewMapper {

  private final SkillMapper skillMapper;

  public StudentProgressViewMapper(SkillMapper skillMapper) {
    this.skillMapper = skillMapper;
  }

  public StudentProgressViewDTO fromDomainToDto(
      StudentProgress studentProgress, List<SkillLevelProgress> skillLevels) {
    return new StudentProgressViewDTO(
        studentProgress.getId(),
        studentProgress.getTrainingPath().getProgram().getName(),
        skillLevels.stream()
            .map(slp -> skillMapper.fromDomainToDto(slp, studentProgress))
            .toList());
  }
}
