package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillLevelDetailedDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillLevelDetailedMapper {
  SkillLevelDetailedDTO fromDomainToDto(SkillLevel skillLevel);
}
