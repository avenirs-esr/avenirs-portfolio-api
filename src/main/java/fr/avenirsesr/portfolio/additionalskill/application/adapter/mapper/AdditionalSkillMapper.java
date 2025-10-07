package fr.avenirsesr.portfolio.additionalskill.application.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillDTO;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;

public interface AdditionalSkillMapper {
  static AdditionalSkillDTO toAdditionalSkillDTO(AdditionalSkill additionalSkill) {
    return new AdditionalSkillDTO(
        additionalSkill.getId(),
        additionalSkill.getLibelle(),
        additionalSkill.getCategoryPath().stream()
            .map(AdditionalSkillCategory::getLibelle)
            .toList(),
        additionalSkill.getType());
  }
}
