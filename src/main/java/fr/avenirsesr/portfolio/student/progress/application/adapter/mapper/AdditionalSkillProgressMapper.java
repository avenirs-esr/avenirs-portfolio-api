package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.mapper.AdditionalSkillCategoryMapper;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.AdditionalSkillProgressDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.AdditionalSkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.domain.data.AdditionalSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;

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
      AdditionalSkillProgressDetails additionalSkillProgressDetails) {
    return new AdditionalSkillProgressDetailsDTO(
        additionalSkillProgressDetails.additionalSkillProgress().getId(),
        additionalSkillProgressDetails.additionalSkillProgress().getSkill().getLibelle(),
        additionalSkillProgressDetails
            .additionalSkillProgress()
            .getSkill()
            .getCategoryPath()
            .stream()
            .map(AdditionalSkillCategoryMapper::toDTO)
            .toList(),
        additionalSkillProgressDetails.additionalSkillProgress().getDescription(),
        additionalSkillProgressDetails.additionalSkillProgress().getSkill().getType(),
        additionalSkillProgressDetails.additionalSkillProgress().getLevel(),
        additionalSkillProgressDetails.tracesWithProjectName().stream()
            .map(
                tracesWithProjectName ->
                    TraceOverviewMapper.toDTO(
                        tracesWithProjectName.trace(), tracesWithProjectName.programName()))
            .toList(),
        additionalSkillProgressDetails.additionalSkillProgress().getCreatedAt(),
        additionalSkillProgressDetails.additionalSkillProgress().getUpdatedAt());
  }
}
