package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.ETraceAssociationType;
import fr.avenirsesr.portfolio.trace.application.adapter.response.TraceAssociationSearchResult;

public interface TraceAssociationSearchResultMapper {
  static TraceAssociationSearchResult toDTO(AMS ams) {
    return new TraceAssociationSearchResult(
        ETraceAssociationType.AMS, ams.getId(), ams.getTitle(), null);
  }

  static TraceAssociationSearchResult toDTO(SkillLevelProgress skillLevelProgress) {
    return new TraceAssociationSearchResult(
        ETraceAssociationType.SKILL_LEVEL,
        skillLevelProgress.getId(),
        skillLevelProgress.getSkillLevel().getSkill().getName(),
        skillLevelProgress.getSkillLevel().getName());
  }

  static TraceAssociationSearchResult toDTO(DeclaredSkillProgress declaredSkillProgress) {
    return new TraceAssociationSearchResult(
        ETraceAssociationType.DECLARED_SKILL,
        declaredSkillProgress.getId(),
        declaredSkillProgress.getSkill().getLibelle(),
        declaredSkillProgress.getSkill().getType().name());
  }
}
