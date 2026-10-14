package fr.avenirsesr.portfolio.student.association.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationLimitData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.exception.AssociationAlreadyExistException;
import fr.avenirsesr.portfolio.student.association.domain.exception.AssociationDoesNotExistException;
import fr.avenirsesr.portfolio.student.association.domain.exception.MaximumAssociationReachedException;
import fr.avenirsesr.portfolio.student.association.domain.filter.AssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.port.output.repository.AssociationRepository;
import fr.avenirsesr.portfolio.student.association.domain.port.output.strategy.AssociationStrategy;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssociationServiceImpl implements AssociationService {
  private final AssociationRepository associationRepository;
  private final List<AssociationStrategy> strategies;

  @Override
  public List<Association> createAll(List<AssociationData> associationsData) {
    if (!associationRepository.findAllIn(associationsData).isEmpty()) {
      throw new AssociationAlreadyExistException();
    }

    var associations =
        associationsData.stream()
            .map(
                association ->
                    Association.create(
                        association.id1(), association.id2(), association.associationType()))
            .toList();

    return associationRepository.saveAll(associations);
  }

  @Override
  public List<Association> getAllOf(
      UUID id, Class<?> clazz, List<EAssociationType> associationTypes) {
    return associationRepository.findAllOf(id, clazz, associationTypes);
  }

  @Override
  public List<Association> getAllOf(
      List<UUID> ids, Class<?> clazz, List<EAssociationType> associationTypes) {
    if (ids.isEmpty()) {
      return List.of();
    }

    return associationRepository.findAllOf(ids, clazz, associationTypes);
  }

  @Override
  public Map<UUID, Long> countAllOf(
      List<UUID> ids, Class<?> clazz, EAssociationType associationType) {
    if (ids.isEmpty()) {
      return Map.of();
    }

    return associationRepository.countAllOf(ids, clazz, associationType);
  }

  @Override
  public AssociatedElementsData getAllAssociatedElementsOf(
      UUID id, EAssociationContextType contextType, boolean onlyNotCompleted) {
    strategyOf(contextType).checkLoggedInStudentOwns(List.of(id));

    var clazz = contextType.toClass();

    return associationsByAssociatedClass(
            getAllOf(id, clazz, EAssociationType.getAllBy(clazz)), clazz)
        .entrySet()
        .stream()
        .map(
            entry ->
                strategyOf(EAssociationContextType.of(entry.getKey()))
                    .toAssociatedElements(entry.getValue(), clazz, onlyNotCompleted))
        .reduce(AssociatedElementsData.empty(), AssociatedElementsData::merge);
  }

  @Override
  public List<AssociationLimitData> getAssociationLimits(
      UUID id, EAssociationContextType contextType) {
    strategyOf(contextType).checkLoggedInStudentOwns(List.of(id));

    return EAssociationType.getAllBy(contextType.toClass()).stream()
        .map(associationType -> limitsOf(contextType, List.of(id), associationType).get(id))
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public AssociatedElementsData associate(
      UUID id,
      EAssociationContextType contextType,
      EAssociationContextType associatedContextType,
      List<UUID> associatedIds) {
    var associationType =
        EAssociationType.of(contextType.toClass(), associatedContextType.toClass());
    var uniqueAssociatedIds = associatedIds.stream().distinct().toList();

    strategyOf(contextType)
        .checkLoggedInStudentCanAssociate(id, associationType, uniqueAssociatedIds.size());
    strategyOf(associatedContextType).checkLoggedInStudentOwns(uniqueAssociatedIds);

    checkLimits(contextType, List.of(id), associationType, uniqueAssociatedIds.size());
    checkLimits(associatedContextType, uniqueAssociatedIds, associationType, 1);

    createAll(
        uniqueAssociatedIds.stream()
            .map(
                associatedId ->
                    associationDataOf(id, contextType.toClass(), associatedId, associationType))
            .toList());

    return getAllAssociatedElementsOf(id, contextType, false);
  }

  @Override
  public void unassociate(UUID id, EAssociationContextType contextType, List<UUID> associationIds) {
    strategyOf(contextType).checkLoggedInStudentCanUnassociate(List.of(id));

    var clazz = contextType.toClass();
    var associations =
        getAllOf(id, clazz, EAssociationType.getAllBy(clazz)).stream()
            .filter(association -> associationIds.contains(association.getId()))
            .toList();

    if (!new HashSet<>(associations.stream().map(Association::getId).toList())
        .containsAll(associationIds)) {
      throw new AssociationDoesNotExistException();
    }

    associationsByAssociatedClass(associations, clazz)
        .forEach(
            (associatedClass, associatedAssociations) ->
                strategyOf(EAssociationContextType.of(associatedClass))
                    .checkLoggedInStudentCanUnassociate(
                        associatedIdsOf(associatedAssociations, clazz)));

    deleteAllByIds(associationIds);
  }

  @Override
  public PagedResult<AssociationSearchResultData> searchForAssociation(
      UUID id,
      EAssociationContextType contextType,
      EAssociationContextType associatedContextType,
      String keyword,
      AssociationSearchFilter filter,
      PageCriteria pageCriteria) {
    var associationType =
        EAssociationType.of(contextType.toClass(), associatedContextType.toClass());
    Set<UUID> associatedIds;

    if (id == null) {
      associatedIds = Set.of();
    } else {
      strategyOf(contextType).checkLoggedInStudentOwns(List.of(id));
      associatedIds =
          new HashSet<>(
              associatedIdsOf(
                  getAllOf(id, contextType.toClass(), List.of(associationType)),
                  contextType.toClass()));
    }

    var searchResults = strategyOf(associatedContextType).search(keyword, filter, pageCriteria);
    var associableIds =
        searchResults.content().stream()
            .filter(result -> !result.disabled() && !associatedIds.contains(result.id()))
            .map(AssociationSearchResultData::id)
            .toList();
    var limits = limitsOf(associatedContextType, associableIds, associationType);

    return new PagedResult<>(
        searchResults.content().stream()
            .map(
                result -> {
                  var limit = limits.get(result.id());
                  var limitReached = limit != null && limit.remaining() == 0;

                  return new AssociationSearchResultData(
                      result.id(),
                      result.title(),
                      result.category(),
                      result.disabled() || associatedIds.contains(result.id()) || limitReached,
                      limitReached,
                      result.description());
                })
            .toList(),
        searchResults.pageInfo());
  }

  @Override
  public void deleteAllByIds(List<UUID> ids) {
    var activities = associationRepository.findAllById(ids);

    if (!new HashSet<>(activities.stream().map(Association::getId).toList()).containsAll(ids)) {
      throw new AssociationDoesNotExistException();
    }

    associationRepository.removeAllFromDatabase(activities);
  }

  @Override
  public void deleteAllOf(List<UUID> ids, Class<?> clazz) {
    var associationTypes = EAssociationType.getAllBy(clazz);
    var associations =
        ids.stream().flatMap(id -> getAllOf(id, clazz, associationTypes).stream()).toList();
    deleteAllByIds(associations.stream().map(Association::getId).toList());
  }

  private AssociationStrategy strategyOf(EAssociationContextType contextType) {
    return strategies.stream()
        .filter(strategy -> strategy.getContextType() == contextType)
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(contextType + " has no association strategy"));
  }

  private Map<Class<?>, List<Association>> associationsByAssociatedClass(
      List<Association> associations, Class<?> clazz) {
    return associations.stream()
        .collect(
            Collectors.groupingBy(
                association -> association.getAssociationType().associatedKeyOf(clazz)));
  }

  private List<UUID> associatedIdsOf(List<Association> associations, Class<?> clazz) {
    return associations.stream().map(association -> association.associatedIdOf(clazz)).toList();
  }

  private AssociationData associationDataOf(
      UUID id, Class<?> clazz, UUID associatedId, EAssociationType associationType) {
    return associationType.getKey1().equals(clazz)
        ? new AssociationData(id, associatedId, associationType)
        : new AssociationData(associatedId, id, associationType);
  }

  private Map<UUID, AssociationLimitData> limitsOf(
      EAssociationContextType contextType, List<UUID> ids, EAssociationType associationType) {
    if (ids.isEmpty()) {
      return Map.of();
    }

    var limits = strategyOf(contextType).getAssociationLimits(ids, associationType);

    if (limits.isEmpty()) {
      return Map.of();
    }

    var associatedContextType =
        EAssociationContextType.of(associationType.associatedKeyOf(contextType.toClass()));
    var counts = countAllOf(List.copyOf(limits.keySet()), contextType.toClass(), associationType);

    return limits.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                limit ->
                    new AssociationLimitData(
                        associatedContextType,
                        limit.getValue(),
                        counts.getOrDefault(limit.getKey(), 0L))));
  }

  private void checkLimits(
      EAssociationContextType contextType,
      List<UUID> ids,
      EAssociationType associationType,
      int associationsToAdd) {
    if (limitsOf(contextType, ids, associationType).values().stream()
        .anyMatch(limit -> associationsToAdd > limit.remaining())) {
      throw new MaximumAssociationReachedException();
    }
  }
}
