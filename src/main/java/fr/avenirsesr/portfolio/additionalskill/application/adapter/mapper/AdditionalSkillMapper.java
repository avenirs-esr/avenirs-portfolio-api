package fr.avenirsesr.portfolio.additionalskill.application.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillDTO;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import java.util.List;

public interface AdditionalSkillMapper {
  static AdditionalSkillDTO toAdditionalSkillDTO(AdditionalSkill additionalSkill) {
    return new AdditionalSkillDTO(
        additionalSkill.getId().toString(),
        additionalSkill.getPathSegments().getSkill().getLibelle(),
        List.of(
            additionalSkill.getPathSegments().getIssue().getLibelle(),
            additionalSkill.getPathSegments().getTarget().getLibelle(),
            additionalSkill.getPathSegments().getMacroSkill().getLibelle()),
        additionalSkill.getType().getValue());
  }
}
