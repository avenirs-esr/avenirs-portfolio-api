package fr.avenirsesr.portfolio.student.experience.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.filter.AssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.output.strategy.AssociationStrategy;
import fr.avenirsesr.portfolio.student.association.domain.utils.AssociationUtils;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceAssociationData;
import fr.avenirsesr.portfolio.student.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeclaredExperienceAssociationStrategy implements AssociationStrategy {
  private final DeclaredExperienceService declaredExperienceService;
  private final LoggedInUserService loggedInUserService;

  @Override
  public EAssociationContextType getContextType() {
    return EAssociationContextType.DECLARED_EXPERIENCE;
  }

  @Override
  public void checkLoggedInStudentOwns(List<UUID> elementIds) {
    fetchAndCheckOwnership(elementIds);
  }

  @Override
  public void checkLoggedInStudentCanAssociate(
      UUID elementId, EAssociationType associationType, int associationsToAdd) {
    checkLoggedInStudentOwns(List.of(elementId));
  }

  @Override
  public void checkLoggedInStudentCanUnassociate(List<UUID> elementIds) {
    checkLoggedInStudentOwns(elementIds);
  }

  @Override
  public PagedResult<AssociationSearchResultData> search(
      String keyword, AssociationSearchFilter filter, PageCriteria pageCriteria) {
    var declaredExperiences = declaredExperienceService.search(keyword, pageCriteria);

    return new PagedResult<>(
        declaredExperiences.content().stream()
            .map(
                declaredExperience ->
                    new AssociationSearchResultData(
                        declaredExperience.getId(),
                        declaredExperience.getTitle(),
                        declaredExperience.getExperienceType() != null
                            ? declaredExperience.getExperienceType().name()
                            : null,
                        false))
            .toList(),
        declaredExperiences.pageInfo());
  }

  @Override
  public AssociatedElementsData toAssociatedElements(
      List<Association> associations, Class<?> subjectClass, boolean onlyNotCompleted) {
    var declaredExperiences =
        declaredExperienceService.findAllByIds(
            associations.stream()
                .map(association -> association.associatedIdOf(subjectClass))
                .toList());

    return AssociatedElementsData.ofDeclaredExperiences(
        associations.stream()
            .map(
                association ->
                    new DeclaredExperienceAssociationData(
                        association.getId(),
                        AssociationUtils.elementOf(
                            declaredExperiences,
                            association.associatedIdOf(subjectClass),
                            DeclaredExperienceNotFoundException::new)))
            .toList());
  }

  private List<DeclaredExperience> fetchAndCheckOwnership(List<UUID> elementIds) {
    var declaredExperiences = declaredExperienceService.findAllByIds(elementIds);

    AssociationUtils.checkOwnership(
        elementIds,
        declaredExperiences,
        DeclaredExperience::getStudent,
        loggedInUserService.getLoggedInStudent(),
        DeclaredExperienceNotFoundException::new);

    return declaredExperiences;
  }
}
