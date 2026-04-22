package fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input;

import fr.avenirsesr.portfolio.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationsData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import java.util.List;
import java.util.UUID;

public interface DeclaredSkillProgressService {

  PagedResult<DeclaredSkillProgress> getDeclaredSkillsProgresses(PageCriteria criteria);

  DeclaredSkillProgress createDeclaredSkillProgress(
      UUID declaredSkillId, EExternalSkillType type, EDeclaredSkillLevel level, String reflection);

  DeclaredSkillProgress updateDeclaredSkillProgress(
      UUID declaredSkillProgressId, EDeclaredSkillLevel level, String reflection);

  DeclaredSkillProgressDetails getDeclaredSkillProgressDetails(UUID declaredSkillProgressId);

  void deleteDeclaredSkillProgresses(List<UUID> declaredSkillProgressIds);

  PagedResult<DeclaredSkillProgress> searchDeclaredSkill(String keyword, PageCriteria pageCriteria);

  PagedResult<AssociationSearchResultData> searchDeclaredSkillsForAssociation(
      UUID excludeAssociatedWithElementId,
      EAssociationContextType contextType,
      String keyword,
      PageCriteria pageCriteria);

  PagedResult<AssociationSearchResultData> searchDeclaredActivityForAssociation(
      UUID declaredSkillProgressId, String keyword, PageCriteria pageCriteria);

  void unassociateTraces(UUID declaredSkillProgressId, List<UUID> traceIds);

  List<DeclaredSkillProgress> findAllDeclaredSkillProgressesByIds(List<UUID> ids);

  DeclaredSkillAssociationsData getAssociationsOf(UUID declaredSkillId);

  DeclaredSkillAssociationsData associateDeclaredSkillWithActivities(
      UUID declaredSkillIId, List<UUID> declaredActivityIds);
}
