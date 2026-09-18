package fr.avenirsesr.portfolio.student.skill.domain.service;

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
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationData;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeclaredSkillAssociationStrategy implements AssociationStrategy {
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final LoggedInUserService loggedInUserService;

  @Override
  public EAssociationContextType getContextType() {
    return EAssociationContextType.DECLARED_SKILL;
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
    var declaredSkillProgresses =
        declaredSkillProgressService.searchDeclaredSkill(keyword, pageCriteria);

    return new PagedResult<>(
        declaredSkillProgresses.content().stream()
            .map(
                declaredSkillProgress ->
                    new AssociationSearchResultData(
                        declaredSkillProgress.getId(),
                        declaredSkillProgress.getSkill().getLibelle(),
                        declaredSkillProgress.getSkill().getType().name(),
                        false))
            .toList(),
        declaredSkillProgresses.pageInfo());
  }

  @Override
  public AssociatedElementsData toAssociatedElements(
      List<Association> associations, Class<?> subjectClass, boolean onlyNotCompleted) {
    var declaredSkillProgresses =
        declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
            associations.stream()
                .map(association -> association.associatedIdOf(subjectClass))
                .toList());

    return AssociatedElementsData.ofDeclaredSkills(
        associations.stream()
            .map(
                association ->
                    new DeclaredSkillAssociationData(
                        association.getId(),
                        AssociationUtils.elementOf(
                            declaredSkillProgresses,
                            association.associatedIdOf(subjectClass),
                            DeclaredSkillProgressNotFoundException::new)))
            .toList());
  }

  private List<DeclaredSkillProgress> fetchAndCheckOwnership(List<UUID> elementIds) {
    var declaredSkillProgresses =
        declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(elementIds);

    AssociationUtils.checkOwnership(
        elementIds,
        declaredSkillProgresses,
        DeclaredSkillProgress::getStudent,
        loggedInUserService.getLoggedInStudent(),
        DeclaredSkillProgressNotFoundException::new);

    return declaredSkillProgresses;
  }
}
