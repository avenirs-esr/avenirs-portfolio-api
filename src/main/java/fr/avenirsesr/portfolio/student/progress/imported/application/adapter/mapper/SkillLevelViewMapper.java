package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillLevelViewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SkillLevelViewMapper {

  @Mapping(source = "skillLevel.id", target = "id")
  @Mapping(source = "skillLevel.name", target = "name")
  @Mapping(source = "skillLevel.description", target = "shortDescription")
  SkillLevelViewDTO fromDomainToDto(SkillLevelProgress skillLevelProgress);

  default String unwrap(Optional<String> value) {
    return value == null ? null : value.orElse(null);
  }
}
