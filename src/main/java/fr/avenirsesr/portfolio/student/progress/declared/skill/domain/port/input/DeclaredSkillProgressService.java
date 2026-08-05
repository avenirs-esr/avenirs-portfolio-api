package fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input;

import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationsData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillProgressData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import java.util.List;
import java.util.UUID;

public interface DeclaredSkillProgressService {

  PagedResult<DeclaredSkillProgressData> getDeclaredSkillsProgresses(
      PageCriteria criteria, Boolean isValorized);

  DeclaredSkillProgress createDeclaredSkillProgress(
      UUID declaredSkillId, EExternalSkillType type, EDeclaredSkillLevel level, String reflection);

  DeclaredSkillProgress updateDeclaredSkillProgress(
      UUID declaredSkillProgressId,
      EDeclaredSkillLevel level,
      String reflection,
      boolean valorized);

  DeclaredSkillProgressDetails getDeclaredSkillProgressDetails(UUID declaredSkillProgressId);

  void deleteDeclaredSkillProgresses(List<UUID> declaredSkillProgressIds);

  PagedResult<DeclaredSkillProgress> searchDeclaredSkill(String keyword, PageCriteria pageCriteria);

  PagedResult<AssociationSearchResultData> searchDeclaredSkillsForAssociation(
      UUID excludeAssociatedWithElementId,
      EAssociationContextType contextType,
      String keyword,
      PageCriteria pageCriteria);

  List<DeclaredSkillProgress> findAllDeclaredSkillProgressesByIds(List<UUID> ids);

  DeclaredSkillAssociationsData getAssociationsOf(UUID declaredSkillId);

  DeclaredSkillAssociationsData associateDeclaredSkillWithActivities(
      UUID declaredSkillIId, List<UUID> declaredActivityIds);

  DeclaredSkillAssociationsData associateDeclaredSkillWithDeclaredExperiences(
      UUID declaredSkillId, List<UUID> declaredExperienceIds);

  void deleteAssociations(UUID declaredSkillProgressId, List<UUID> idsToDelete);
}
