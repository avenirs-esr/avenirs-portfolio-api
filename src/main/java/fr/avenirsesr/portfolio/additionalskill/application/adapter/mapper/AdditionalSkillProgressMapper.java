package fr.avenirsesr.portfolio.additionalskill.application.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillProgressDTO;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import java.util.List;

public interface AdditionalSkillProgressMapper {
  static AdditionalSkillProgressDTO toAdditionalSkillProgressDTO(
      AdditionalSkillProgress additionalSkillProgress) {
    return new AdditionalSkillProgressDTO(
        additionalSkillProgress.getId(),
        additionalSkillProgress.getSkill().getPathSegments().getSkill().getLibelle(),
        List.of(
            additionalSkillProgress.getSkill().getPathSegments().getIssue().getLibelle(),
            additionalSkillProgress.getSkill().getPathSegments().getTarget().getLibelle(),
            additionalSkillProgress.getSkill().getPathSegments().getMacroSkill().getLibelle()),
        additionalSkillProgress.getSkill().getType().getValue(),
        additionalSkillProgress.getLevel());
  }
}
