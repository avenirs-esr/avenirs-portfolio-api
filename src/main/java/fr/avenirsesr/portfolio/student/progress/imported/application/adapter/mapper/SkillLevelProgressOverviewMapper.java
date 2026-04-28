package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillLevelProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SkillLevelProgressOverviewMapper {

  @Mapping(source = "skillLevel.id", target = "id")
  @Mapping(source = "skillLevel.name", target = "name")
  SkillLevelProgressOverviewDTO fromDomainToDto(SkillLevelProgress skillLevelProgress);
}
