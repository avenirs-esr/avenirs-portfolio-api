package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillLevelProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = SkillLevelProgressOverviewMapper.class)
public interface SkillOverviewMapper {

  SkillLevelProgressOverviewDTO toSkillLevelProgressOverview(SkillLevelProgress skillLevelProgress);

  default SkillOverviewDTO fromDomainToDto(SkillLevelProgress skillLevelProgress) {
    return new SkillOverviewDTO(
        skillLevelProgress.getSkillLevel().getSkill().getId(),
        skillLevelProgress.getSkillLevel().getSkill().getName(),
        toSkillLevelProgressOverview(skillLevelProgress));
  }
}
