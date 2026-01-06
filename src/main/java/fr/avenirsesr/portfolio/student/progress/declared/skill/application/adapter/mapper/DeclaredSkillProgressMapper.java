package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import fr.avenirsesr.portfolio.declaredskill.application.adapter.dto.DeclaredSkillCategoryDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillProgressDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;
import java.util.List;

public interface DeclaredSkillProgressMapper {
  static DeclaredSkillProgressDTO toDeclaredSkillProgressDTO(
      DeclaredSkillProgress declaredSkillProgress) {
    return new DeclaredSkillProgressDTO(
        declaredSkillProgress.getId(),
        declaredSkillProgress.getSkill().getLibelle(),
        declaredSkillProgress.getSkill().getPathSegments(),
        declaredSkillProgress.getSkill().getType(),
        declaredSkillProgress.getLevel(),
        declaredSkillProgress.getDescription());
  }

  static DeclaredSkillProgressDetailsDTO toDeclaredSkillProgressDetailsDTO(
      DeclaredSkillProgressDetails declaredSkillProgressDetails) {
    List<DeclaredSkillCategoryDTO> categories =
        declaredSkillProgressDetails.externalCategories().stream()
            .map(
                externalCat ->
                    new DeclaredSkillCategoryDTO(
                        externalCat.libelle(),
                        EExternalSkillCategoryType.valueOf(externalCat.type().name())))
            .toList();

    return new DeclaredSkillProgressDetailsDTO(
        declaredSkillProgressDetails.declaredSkillProgress().getId(),
        declaredSkillProgressDetails.declaredSkillProgress().getSkill().getLibelle(),
        categories,
        declaredSkillProgressDetails.declaredSkillProgress().getDescription(),
        declaredSkillProgressDetails.declaredSkillProgress().getSkill().getType(),
        declaredSkillProgressDetails.declaredSkillProgress().getLevel(),
        declaredSkillProgressDetails.tracesWithProjectName().stream()
            .map(
                tracesWithProjectName ->
                    TraceOverviewMapper.toDTO(
                        tracesWithProjectName.trace(), tracesWithProjectName.programName()))
            .toList(),
        declaredSkillProgressDetails.declaredSkillProgress().getCreatedAt(),
        declaredSkillProgressDetails.declaredSkillProgress().getUpdatedAt());
  }
}
