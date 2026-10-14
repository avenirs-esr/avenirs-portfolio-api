package fr.avenirsesr.portfolio.student.activity.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityAlreadyFinishedException;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityUnsubscribedException;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.filter.AssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.output.strategy.AssociationStrategy;
import fr.avenirsesr.portfolio.student.association.domain.utils.AssociationUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeclaredActivityAssociationStrategy implements AssociationStrategy {
  private static final int TRACE_UNLIMITED_ITERATIONS = -1;

  private final DeclaredActivityService declaredActivityService;
  private final LoggedInUserService loggedInUserService;

  @Override
  public EAssociationContextType getContextType() {
    return EAssociationContextType.DECLARED_ACTIVITY;
  }

  @Override
  public void checkLoggedInStudentOwns(List<UUID> elementIds) {
    fetchAndCheckOwnership(elementIds);
  }

  @Override
  public void checkLoggedInStudentCanAssociate(
      UUID elementId, EAssociationType associationType, int associationsToAdd) {
    var declaredActivity = fetchAndCheckOwnership(List.of(elementId)).getFirst();

    if (declaredActivity.isUnsubscribed()) {
      throw new DeclaredActivityUnsubscribedException();
    }
  }

  @Override
  public void checkLoggedInStudentCanUnassociate(List<UUID> elementIds) {
    if (fetchAndCheckOwnership(elementIds).stream()
        .anyMatch(declaredActivity -> declaredActivity.getFinishedAt().isPresent())) {
      throw new DeclaredActivityAlreadyFinishedException();
    }
  }

  @Override
  public Map<UUID, Integer> getAssociationLimits(
      List<UUID> elementIds, EAssociationType associationType) {
    if (associationType != EAssociationType.DECLARED_ACTIVITY_TRACE) {
      return Map.of();
    }

    return declaredActivityService.findAllDeclaredActivitiesByIds(elementIds).stream()
        .filter(declaredActivity -> traceLimitOf(declaredActivity) != TRACE_UNLIMITED_ITERATIONS)
        .collect(Collectors.toMap(DeclaredActivity::getId, this::traceLimitOf));
  }

  @Override
  public PagedResult<AssociationSearchResultData> search(
      String keyword, AssociationSearchFilter filter, PageCriteria pageCriteria) {
    var declaredActivities = declaredActivityService.searchDeclaredActivity(keyword, pageCriteria);

    return new PagedResult<>(
        declaredActivities.content().stream()
            .map(
                declaredActivity ->
                    new AssociationSearchResultData(
                        declaredActivity.getId(),
                        declaredActivity.getActivity().getTitle(),
                        declaredActivity.getActivity().getThematic().name(),
                        declaredActivity.getFinishedAt().isPresent()))
            .toList(),
        declaredActivities.pageInfo());
  }

  @Override
  public AssociatedElementsData toAssociatedElements(
      List<Association> associations, Class<?> subjectClass, boolean onlyNotCompleted) {
    var ids =
        associations.stream().map(association -> association.associatedIdOf(subjectClass)).toList();
    var declaredActivities =
        onlyNotCompleted
            ? declaredActivityService.findAllNotCompletedActivitiesByIds(ids)
            : declaredActivityService.findAllDeclaredActivitiesByIds(ids);
    var statuses = declaredActivityService.getDeclaredActivityStatus(declaredActivities);

    return AssociatedElementsData.ofDeclaredActivities(
        associations.stream()
            .map(
                association -> {
                  var declaredActivity =
                      AssociationUtils.elementOf(
                          declaredActivities,
                          association.associatedIdOf(subjectClass),
                          DeclaredActivityNotFoundException::new);

                  return new DeclaredActivityAssociationData(
                      association.getId(), declaredActivity, statuses.get(declaredActivity));
                })
            .toList());
  }

  private List<DeclaredActivity> fetchAndCheckOwnership(List<UUID> elementIds) {
    var declaredActivities = declaredActivityService.findAllDeclaredActivitiesByIds(elementIds);

    AssociationUtils.checkOwnership(
        elementIds,
        declaredActivities,
        DeclaredActivity::getStudent,
        loggedInUserService.getLoggedInStudent(),
        DeclaredActivityNotFoundException::new);

    return declaredActivities;
  }

  private int traceLimitOf(DeclaredActivity declaredActivity) {
    return declaredActivity.getActivity().getTraceAllowedAssociations();
  }
}
