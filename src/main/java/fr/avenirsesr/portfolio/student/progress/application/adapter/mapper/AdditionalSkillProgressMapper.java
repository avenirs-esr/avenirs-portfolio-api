package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.mapper.AdditionalSkillCategoryMapper;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.AdditionalSkillProgressDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.AdditionalSkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceOverviewDTO;
import java.util.List;

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

  static AdditionalSkillProgressDetailsDTO toAdditionalSkillProgressDetailsDTO(
      AdditionalSkillProgress additionalSkillProgress, List<TraceOverviewDTO> traceOverviewDTOs) {
    return new AdditionalSkillProgressDetailsDTO(
        additionalSkillProgress.getId(),
        additionalSkillProgress.getSkill().getLibelle(),
        additionalSkillProgress.getSkill().getCategoryPath().stream()
            .map(AdditionalSkillCategoryMapper::toDTO)
            .toList(),
        additionalSkillProgress.getDescription(),
        additionalSkillProgress.getSkill().getType(),
        additionalSkillProgress.getLevel(),
        traceOverviewDTOs,
        additionalSkillProgress.getCreatedAt(),
        additionalSkillProgress.getUpdatedAt());
  }
}
