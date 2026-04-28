package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.StudentProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = SkillOverviewMapper.class)
public interface StudentProgressOverviewMapper {

  @Mapping(source = "skillLevel.skill.id", target = "id")
  @Mapping(source = "skillLevel.skill.name", target = "name")
  @Mapping(source = "skillLevelProgress", target = "currentSkillLevel")
  SkillOverviewDTO toSkillOverviewDTO(SkillLevelProgress skillLevelProgress);

  default StudentProgressOverviewDTO fromDomainToDto(
      StudentProgress studentProgress, List<SkillLevelProgress> skillLevelToDisplay) {
    return new StudentProgressOverviewDTO(
        studentProgress.getId(),
        studentProgress.getTrainingPath().getProgram().getName(),
        skillLevelToDisplay.stream().map(this::toSkillOverviewDTO).toList());
  }
}
