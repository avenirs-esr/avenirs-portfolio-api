package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
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

  static TraceAssociationSearchResult toDTO(AdditionalSkillProgress additionalSkillProgress) {
    return new TraceAssociationSearchResult(
        ETraceAssociationType.ADDITIONAL_SKILL,
        additionalSkillProgress.getId(),
        additionalSkillProgress.getSkill().getLibelle(),
        additionalSkillProgress.getSkill().getType().name());
  }
}
