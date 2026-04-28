package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillDetailedDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = SkillLevelDetailedMapper.class)
public interface SkillDetailedMapper {

  @Mapping(source = "skill.id", target = "id")
  @Mapping(source = "skill.name", target = "name")
  @Mapping(source = "skillLevels", target = "skillLevels")
  SkillDetailedDTO fromDomainToDto(Skill skill, List<SkillLevel> skillLevels);
}
