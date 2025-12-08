package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillDetailedDTO;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillLevelDetailedDTO;
import java.util.List;

public interface SkillDetailedMapper {
  static SkillDetailedDTO fromDomainToDto(Skill skill, List<SkillLevel> skillLevels) {
    List<SkillLevelDetailedDTO> skillLevelsDetailed =
        skillLevels.stream().map(SkillLevelDetailedMapper::fromDomainToDto).toList();

    return new SkillDetailedDTO(skill.getId(), skill.getName(), skillLevelsDetailed);
  }
}
