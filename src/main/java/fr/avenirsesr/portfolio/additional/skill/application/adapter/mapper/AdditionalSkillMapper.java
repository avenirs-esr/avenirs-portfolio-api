package fr.avenirsesr.portfolio.additional.skill.application.adapter.mapper;

import fr.avenirsesr.portfolio.additional.skill.application.adapter.dto.AdditionalSkillDTO;
import fr.avenirsesr.portfolio.additional.skill.domain.model.AdditionalSkill;
import java.util.List;

public interface AdditionalSkillMapper {
  static AdditionalSkillDTO toAdditionalSkillDTO(AdditionalSkill additionalSkill) {
    return new AdditionalSkillDTO(
        additionalSkill.getId(),
        additionalSkill.getPathSegments().getSkill().getLibelle(),
        List.of(
            additionalSkill.getPathSegments().getIssue().getLibelle(),
            additionalSkill.getPathSegments().getTarget().getLibelle(),
            additionalSkill.getPathSegments().getMacroSkill().getLibelle()),
        additionalSkill.getType().getValue());
  }
}
