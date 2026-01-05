package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillCategoryDTO;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.AdditionalSkillProgressDTO;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.AdditionalSkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.data.AdditionalSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;
import java.util.List;

public interface AdditionalSkillProgressMapper {
  static AdditionalSkillProgressDTO toAdditionalSkillProgressDTO(
      AdditionalSkillProgress additionalSkillProgress) {
    return new AdditionalSkillProgressDTO(
        additionalSkillProgress.getId(),
        additionalSkillProgress.getSkill().getLibelle(),
        additionalSkillProgress.getSkill().getPathSegments(),
        additionalSkillProgress.getSkill().getType(),
        additionalSkillProgress.getLevel(),
        additionalSkillProgress.getDescription());
  }

  static AdditionalSkillProgressDetailsDTO toAdditionalSkillProgressDetailsDTO(
      AdditionalSkillProgressDetails additionalSkillProgressDetails) {
    List<AdditionalSkillCategoryDTO> categories =
        additionalSkillProgressDetails.externalCategories().stream()
            .map(
                externalCat ->
                    new AdditionalSkillCategoryDTO(
                        externalCat.libelle(),
                        EExternalSkillCategoryType.valueOf(externalCat.type().name())))
            .toList();

    return new AdditionalSkillProgressDetailsDTO(
        additionalSkillProgressDetails.additionalSkillProgress().getId(),
        additionalSkillProgressDetails.additionalSkillProgress().getSkill().getLibelle(),
        categories,
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
