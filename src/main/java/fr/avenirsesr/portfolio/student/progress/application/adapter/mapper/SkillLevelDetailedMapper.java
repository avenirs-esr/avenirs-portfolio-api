package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillLevelDetailedDTO;

public interface SkillLevelDetailedMapper {
  static SkillLevelDetailedDTO fromDomainToDto(SkillLevel skillLevel) {
    return new SkillLevelDetailedDTO(skillLevel.getId(), skillLevel.getName());
  }
}
