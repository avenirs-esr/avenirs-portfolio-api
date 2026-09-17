package fr.avenirsesr.portfolio.student.association.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityAlreadyFinishedException;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.exception.AssociationAlreadyExistException;
import fr.avenirsesr.portfolio.student.association.domain.exception.AssociationDoesNotExistException;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.port.output.repository.AssociationRepository;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceAssociationData;
import fr.avenirsesr.portfolio.student.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationData;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceAssociationData;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceViewData;
import fr.avenirsesr.portfolio.student.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.student.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssociationServiceImpl implements AssociationService {
  private final AssociationRepository associationRepository;
  private final LoggedInUserService loggedInUserService;
  private final TraceService traceService;
  private final DeclaredActivityService declaredActivityService;
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final DeclaredExperienceService declaredExperienceService;

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
  public AssociatedElementsData getAllAssociatedElementsOf(UUID id, Class<?> clazz) {
    return getAllAssociatedElementsOf(id, clazz, false);
  }

  @Override
  public AssociatedElementsData getAllAssociatedElementsOf(
      UUID id, Class<?> clazz, boolean onlyNotCompletedActivities) {
    Map<Class<?>, List<Association>> associationsByAssociatedClass =
        getAllOf(id, clazz, EAssociationType.getAllBy(clazz)).stream()
            .collect(
                Collectors.groupingBy(
                    association -> association.getAssociationType().associatedKeyOf(clazz)));

    return new AssociatedElementsData(
        toTraceAssociations(associationsOf(associationsByAssociatedClass, Trace.class), clazz),
        toDeclaredActivityAssociations(
            associationsOf(associationsByAssociatedClass, DeclaredActivity.class),
            clazz,
            onlyNotCompletedActivities),
        toDeclaredSkillAssociations(
            associationsOf(associationsByAssociatedClass, DeclaredSkillProgress.class), clazz),
        toDeclaredExperienceAssociations(
            associationsOf(associationsByAssociatedClass, DeclaredExperience.class), clazz));
  }

  @Override
  public void associate(
      UUID id, Class<?> clazz, List<UUID> associatedIds, EAssociationType associationType) {
    var uniqueAssociatedIds = associatedIds.stream().distinct().toList();

    checkAssociatedElements(associationType.associatedKeyOf(clazz), uniqueAssociatedIds);

    createAll(
        uniqueAssociatedIds.stream()
            .map(associatedId -> associationDataOf(id, clazz, associatedId, associationType))
            .toList());
  }

  @Override
  public void unassociate(UUID id, Class<?> clazz, List<UUID> associationIds) {
    var associations =
        getAllOf(id, clazz, EAssociationType.getAllBy(clazz)).stream()
            .filter(association -> associationIds.contains(association.getId()))
            .toList();

    if (!new HashSet<>(associations.stream().map(Association::getId).toList())
        .containsAll(associationIds)) {
      throw new AssociationDoesNotExistException();
    }

    checkIfDeclaredActivitiesAssociationsAreDeletable(associations);

    deleteAllByIds(associationIds);
  }

  @Override
  public PagedResult<AssociationSearchResultData> searchForAssociation(
      UUID id,
      Class<?> clazz,
      EAssociationContextType contextType,
      String keyword,
      PageCriteria pageCriteria) {
    var associationType = EAssociationType.of(clazz, contextType.toClass());

    var associatedIds =
        getAllOf(id, clazz, List.of(associationType)).stream()
            .map(associationType.associatedIdExtractorFor(clazz))
            .collect(Collectors.toSet());

    return switch (contextType) {
      case TRACE ->
          toSearchResults(
              traceService.getTracesView(
                  keyword,
                  new TraceFilter(null, null, null, null),
                  null,
                  pageCriteria,
                  new SortCriteria(ESortField.DATE, ESortOrder.DESC)),
              associatedIds,
              TraceViewData::id,
              TraceViewData::title,
              trace -> null,
              trace -> false);
      case DECLARED_ACTIVITY ->
          toSearchResults(
              declaredActivityService.searchDeclaredActivity(keyword, pageCriteria),
              associatedIds,
              AvenirsBaseModel::getId,
              declaredActivity -> declaredActivity.getActivity().getTitle(),
              declaredActivity -> declaredActivity.getActivity().getThematic().name(),
              declaredActivity -> declaredActivity.getFinishedAt().isPresent());
      case DECLARED_SKILL ->
          toSearchResults(
              declaredSkillProgressService.searchDeclaredSkill(keyword, pageCriteria),
              associatedIds,
              AvenirsBaseModel::getId,
              declaredSkillProgress -> declaredSkillProgress.getSkill().getLibelle(),
              declaredSkillProgress -> declaredSkillProgress.getSkill().getType().name(),
              declaredSkillProgress -> false);
      case DECLARED_EXPERIENCE ->
          toSearchResults(
              declaredExperienceService.search(keyword, pageCriteria),
              associatedIds,
              AvenirsBaseModel::getId,
              DeclaredExperience::getTitle,
              declaredExperience ->
                  declaredExperience.getExperienceType() != null
                      ? declaredExperience.getExperienceType().name()
                      : null,
              declaredExperience -> false);
    };
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

  private <T> PagedResult<AssociationSearchResultData> toSearchResults(
      PagedResult<T> searchResults,
      Set<UUID> associatedIds,
      Function<T, UUID> idExtractor,
      Function<T, String> titleExtractor,
      Function<T, String> categoryExtractor,
      Predicate<T> disabledCondition) {
    return new PagedResult<>(
        searchResults.content().stream()
            .map(
                element ->
                    new AssociationSearchResultData(
                        idExtractor.apply(element),
                        titleExtractor.apply(element),
                        categoryExtractor.apply(element),
                        associatedIds.contains(idExtractor.apply(element))
                            || disabledCondition.test(element)))
            .toList(),
        searchResults.pageInfo());
  }

  private AssociationData associationDataOf(
      UUID id, Class<?> clazz, UUID associatedId, EAssociationType associationType) {
    return associationType.getKey1().equals(clazz)
        ? new AssociationData(id, associatedId, associationType)
        : new AssociationData(associatedId, id, associationType);
  }

  private void checkAssociatedElements(Class<?> clazz, List<UUID> ids) {
    if (Trace.class.equals(clazz)) {
      checkElements(
          ids, traceService.findAllTracesById(ids), Trace::getStudent, TraceNotFoundException::new);
      return;
    }

    if (DeclaredActivity.class.equals(clazz)) {
      checkElements(
          ids,
          declaredActivityService.findAllDeclaredActivitiesByIds(ids),
          DeclaredActivity::getStudent,
          DeclaredActivityNotFoundException::new);
      return;
    }

    if (DeclaredSkillProgress.class.equals(clazz)) {
      checkElements(
          ids,
          declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(ids),
          DeclaredSkillProgress::getStudent,
          DeclaredSkillProgressNotFoundException::new);
      return;
    }

    if (DeclaredExperience.class.equals(clazz)) {
      checkElements(
          ids,
          declaredExperienceService.findAllByIds(ids),
          DeclaredExperience::getStudent,
          DeclaredExperienceNotFoundException::new);
      return;
    }

    throw new IllegalArgumentException(clazz.getSimpleName() + " cannot be associated");
  }

  private <T extends AvenirsBaseModel> void checkElements(
      List<UUID> ids,
      List<T> elements,
      Function<T, Student> studentExtractor,
      Supplier<RuntimeException> notFoundException) {
    if (!new HashSet<>(elements.stream().map(AvenirsBaseModel::getId).toList()).containsAll(ids)) {
      throw notFoundException.get();
    }

    var loggedInStudent = loggedInUserService.getLoggedInStudent();

    if (!elements.stream()
        .allMatch(element -> studentExtractor.apply(element).equals(loggedInStudent))) {
      throw new UserNotAuthorizedException();
    }
  }

  private void checkIfDeclaredActivitiesAssociationsAreDeletable(List<Association> associations) {
    var declaredActivityIds =
        associations.stream()
            .filter(
                association -> association.getAssociationType().involves(DeclaredActivity.class))
            .map(
                association ->
                    association
                        .getAssociationType()
                        .idExtractorFor(DeclaredActivity.class)
                        .apply(association))
            .toList();

    if (declaredActivityIds.isEmpty()) {
      return;
    }

    if (declaredActivityService.findAllDeclaredActivitiesByIds(declaredActivityIds).stream()
        .anyMatch(declaredActivity -> declaredActivity.getFinishedAt().isPresent())) {
      throw new DeclaredActivityAlreadyFinishedException();
    }
  }

  private List<Association> associationsOf(
      Map<Class<?>, List<Association>> associationsByAssociatedClass, Class<?> associatedClass) {
    return associationsByAssociatedClass.getOrDefault(associatedClass, List.of());
  }

  private List<TraceAssociationData> toTraceAssociations(
      List<Association> associations, Class<?> clazz) {
    if (associations.isEmpty()) {
      return List.of();
    }

    var traces = traceService.findAllTracesById(associatedIdsOf(associations, clazz));

    return associations.stream()
        .map(
            association ->
                new TraceAssociationData(
                    association.getId(),
                    associatedElementOf(traces, association, clazz, TraceNotFoundException::new)))
        .toList();
  }

  private List<DeclaredActivityAssociationData> toDeclaredActivityAssociations(
      List<Association> associations, Class<?> clazz, boolean onlyNotCompletedActivities) {
    if (associations.isEmpty()) {
      return List.of();
    }

    var ids = associatedIdsOf(associations, clazz);
    var declaredActivities =
        onlyNotCompletedActivities
            ? declaredActivityService.findAllNotCompletedActivitiesByIds(ids)
            : declaredActivityService.findAllDeclaredActivitiesByIds(ids);
    var statuses = declaredActivityService.getDeclaredActivityStatus(declaredActivities);

    return associations.stream()
        .map(
            association -> {
              var declaredActivity =
                  associatedElementOf(
                      declaredActivities,
                      association,
                      clazz,
                      DeclaredActivityNotFoundException::new);

              return new DeclaredActivityAssociationData(
                  association.getId(), declaredActivity, statuses.get(declaredActivity));
            })
        .toList();
  }

  private List<DeclaredSkillAssociationData> toDeclaredSkillAssociations(
      List<Association> associations, Class<?> clazz) {
    if (associations.isEmpty()) {
      return List.of();
    }

    var declaredSkillProgresses =
        declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
            associatedIdsOf(associations, clazz));

    return associations.stream()
        .map(
            association ->
                new DeclaredSkillAssociationData(
                    association.getId(),
                    associatedElementOf(
                        declaredSkillProgresses,
                        association,
                        clazz,
                        DeclaredSkillProgressNotFoundException::new)))
        .toList();
  }

  private List<DeclaredExperienceAssociationData> toDeclaredExperienceAssociations(
      List<Association> associations, Class<?> clazz) {
    if (associations.isEmpty()) {
      return List.of();
    }

    var declaredExperiences =
        declaredExperienceService.findAllByIds(associatedIdsOf(associations, clazz));

    return associations.stream()
        .map(
            association ->
                new DeclaredExperienceAssociationData(
                    association.getId(),
                    associatedElementOf(
                        declaredExperiences,
                        association,
                        clazz,
                        DeclaredExperienceNotFoundException::new)))
        .toList();
  }

  private List<UUID> associatedIdsOf(List<Association> associations, Class<?> clazz) {
    return associations.stream().map(association -> associatedIdOf(association, clazz)).toList();
  }

  private UUID associatedIdOf(Association association, Class<?> clazz) {
    return association.getAssociationType().associatedIdExtractorFor(clazz).apply(association);
  }

  private <T extends AvenirsBaseModel> T associatedElementOf(
      List<T> elements,
      Association association,
      Class<?> clazz,
      Supplier<RuntimeException> notFoundException) {
    var associatedId = associatedIdOf(association, clazz);

    return elements.stream()
        .filter(element -> element.getId().equals(associatedId))
        .findAny()
        .orElseThrow(notFoundException);
  }
}
