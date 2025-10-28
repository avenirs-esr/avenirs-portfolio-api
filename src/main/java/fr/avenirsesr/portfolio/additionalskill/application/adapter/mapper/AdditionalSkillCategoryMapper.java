package fr.avenirsesr.portfolio.additionalskill.application.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillCategoryDTO;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;

public interface AdditionalSkillCategoryMapper {
  static AdditionalSkillCategoryDTO toDTO(AdditionalSkillCategory additionalSkillCategory) {
    return new AdditionalSkillCategoryDTO(
        additionalSkillCategory.getLibelle(), additionalSkillCategory.getType());
  }
}
