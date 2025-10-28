package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.AdditionalSkillProgressDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.AdditionalSkillProgress;

public interface AdditionalSkillProgressMapper {
  static AdditionalSkillProgressDTO toAdditionalSkillProgressDTO(
      AdditionalSkillProgress additionalSkillProgress) {
    return new AdditionalSkillProgressDTO(
        additionalSkillProgress.getId(),
        additionalSkillProgress.getSkill().getLibelle(),
        additionalSkillProgress.getSkill().getCategoryPath().stream()
            .map(AdditionalSkillCategory::getLibelle)
            .toList(),
        additionalSkillProgress.getSkill().getType(),
        additionalSkillProgress.getLevel(),
        additionalSkillProgress.getDescription());
  }
}
