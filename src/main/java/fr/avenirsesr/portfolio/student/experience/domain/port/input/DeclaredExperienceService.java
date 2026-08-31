package fr.avenirsesr.portfolio.student.experience.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceAssociationsData;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceData;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.model.enums.EExperienceType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DeclaredExperienceService {
  DeclaredExperience create(
      UUID experienceId,
      UUID studentId,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      String result,
      LocalDate startDate,
      LocalDate endDate);

  DeclaredExperience create(
      UUID studentId,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      String result,
      LocalDate startDate,
      LocalDate endDate);

  DeclaredExperience create(
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      String result,
      LocalDate startDate,
      LocalDate endDate);

  DeclaredExperience update(
      UUID experienceId,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      String result,
      LocalDate startDate,
      LocalDate endDate,
      boolean valorized);

  void delete(List<UUID> experienceIds);

  DeclaredExperience get(UUID experienceId);

  PagedResult<DeclaredExperienceData> getView(
      PageCriteria pageCriteria, Boolean isValorized, EExperienceType experienceType);

  List<DeclaredExperience> findAllByIds(List<UUID> experienceIds);

  PagedResult<DeclaredExperience> search(String keyword, PageCriteria pageCriteria);

  DeclaredExperienceAssociationsData getAssociations(UUID experienceId);

  PagedResult<AssociationSearchResultData> searchDeclaredExperiencesForAssociation(
      UUID excludeAssociatedWithElementId,
      EAssociationContextType contextType,
      String keyword,
      PageCriteria pageCriteria);

  DeclaredExperienceAssociationsData associateDeclaredExperienceWithDeclaredSkills(
      UUID declaredExperienceId, List<UUID> declaredSkillProgressIds);

  DeclaredExperienceAssociationsData associateDeclaredExperienceWithTraces(
      UUID declaredExperienceId, List<UUID> traceIds);

  PagedResult<AssociationSearchResultData> searchTracesForAssociation(
      UUID declaredExperienceId, String keyword, PageCriteria pageCriteria, Boolean isAssociated);

  void deleteAssociations(UUID declaredExperienceId, List<UUID> idsToDelete);
}
