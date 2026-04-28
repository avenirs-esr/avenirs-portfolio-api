package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillDTO;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillLevelViewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import java.time.LocalDate;
import java.util.Optional;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = SkillLevelViewMapper.class)
public interface SkillMapper {

  SkillLevelViewDTO toSkillLevelViewDTO(SkillLevelProgress skillLevelProgress);

  default SkillDTO fromDomainToDto(
      SkillLevelProgress skillLevelProgress, StudentProgress studentProgress) {
    var skill = skillLevelProgress.getSkillLevel().getSkill();
    int levelBySkill =
        studentProgress.getAllSkillLevels().stream()
            .filter(s -> s.getSkillLevel().getSkill().equals(skill))
            .toList()
            .size();

    SkillLevelProgress lastAchievedSkillLevel =
        studentProgress.getLastAchievedSkillLevelBySkill().get(skill).orElse(null);

    return new SkillDTO(
        skill.getId(),
        skill.getName(),
        levelBySkill,
        toSkillLevelViewDTO(skillLevelProgress),
        Optional.ofNullable(lastAchievedSkillLevel).map(this::toSkillLevelViewDTO).orElse(null),
        studentProgress.getEndDate().isBefore(LocalDate.now()));
  }
}
