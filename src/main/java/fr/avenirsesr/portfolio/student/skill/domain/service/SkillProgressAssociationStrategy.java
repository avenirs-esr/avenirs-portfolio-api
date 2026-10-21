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
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressAssociationData;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.skill.domain.exception.SkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.SkillProgressService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SkillProgressAssociationStrategy implements AssociationStrategy {
  private final SkillProgressService skillProgressService;
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
    var skillProgresses = skillProgressService.searchSkillProgress(keyword, pageCriteria);

    return new PagedResult<>(
        skillProgresses.content().stream()
            .map(
                skillProgress ->
                    new AssociationSearchResultData(
                        skillProgress.getId(),
                        skillProgress.getSkill().getTitle(),
                        skillProgress.getSkill().getReferential().getType().name(),
                        false))
            .toList(),
        skillProgresses.pageInfo());
  }

  @Override
  public AssociatedElementsData toAssociatedElements(
      List<Association> associations, Class<?> subjectClass, boolean onlyNotCompleted) {
    var skillProgresses =
        skillProgressService.getSkillProgressData(
            skillProgressService.findAllSkillProgressesByIds(
                associations.stream()
                    .map(association -> association.associatedIdOf(subjectClass))
                    .toList()));

    return AssociatedElementsData.ofSkillProgresses(
        associations.stream()
            .map(
                association ->
                    new SkillProgressAssociationData(
                        association.getId(),
                        elementOf(skillProgresses, association.associatedIdOf(subjectClass))))
            .toList());
  }

  private SkillProgressData elementOf(List<SkillProgressData> skillProgresses, UUID id) {
    return skillProgresses.stream()
        .filter(data -> data.skillProgress().getId().equals(id))
        .findAny()
        .orElseThrow(SkillProgressNotFoundException::new);
  }

  private List<SkillProgress> fetchAndCheckOwnership(List<UUID> elementIds) {
    var skillProgresses = skillProgressService.findAllSkillProgressesByIds(elementIds);

    AssociationUtils.checkOwnership(
        elementIds,
        skillProgresses,
        SkillProgress::getStudent,
        loggedInUserService.getLoggedInStudent(),
        SkillProgressNotFoundException::new);

    return skillProgresses;
  }
}
