package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.REFLECTION_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateDateOrder;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateOptionalTextMaxLength;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityAssociationsData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.TraceAssociationSearchInfoData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityAlreadyExistException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityAlreadyFinishedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityDatesException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityHasNotStartedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityStartDateBeforeSubscriptionException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.trace.domain.data.TraceAssociationData;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class DeclaredActivityServiceImpl implements DeclaredActivityService {
  private final DeclaredActivityRepository declaredActivityRepository;
  private final ActivityService activityService;
  private final TraceService traceService;
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final AssociationService associationService;
  private final LoggedInUserService loggedInUserService;

  @Override
  public PagedResult<DeclaredActivity> getDeclaredActivities(PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    var graph = FetchGraph.init().fetch("activity");

    return declaredActivityRepository.findStudentActivitiesByProgressAndDate(
        student, pageCriteria, graph);
  }

  @Override
  public List<DeclaredActivity> getAllDeclaredActivitiesOf(Student student) {
    var graph = FetchGraph.init().fetch("activity");
    return declaredActivityRepository.findAllByStudent(student, graph);
  }

  @Override
  public Optional<DeclaredActivity> getByActivity(Activity activity) {
    Student student = loggedInUserService.getLoggedInStudent();
    return declaredActivityRepository.findByActivity(student, activity);
  }

  @Override
  public DeclaredActivity subscribe(UUID activityId, LocalDate startDate, LocalDate endDate) {
    return subscribe(UUID.randomUUID(), activityId, startDate, endDate);
  }

  @Override
  public DeclaredActivity subscribe(
      UUID declaredActivityId, UUID activityId, LocalDate startDate, LocalDate endDate) {
    Student student = loggedInUserService.getLoggedInStudent();
    Activity activity = activityService.getActivityById(activityId);

    if (declaredActivityRepository.findByActivity(student, activity).isPresent()) {
      throw new DeclaredActivityAlreadyExistException();
    }

    validateActivityDates(startDate, endDate, Instant.now());

    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            declaredActivityId, student, activity, null, null, startDate, endDate, null);
    return declaredActivityRepository.save(declaredActivity);
  }

  @Override
  public void unsubscribeMultiple(List<UUID> activityIds) {
    Student student = loggedInUserService.getLoggedInStudent();

    List<DeclaredActivity> declaredActivities =
        declaredActivityRepository.findAllByActivityIdAndStudent(
            activityIds, student, FetchGraph.init().fetch("activity").add("student").fetch("user"));

    if (!declaredActivities.stream()
        .map(declaredActivity -> declaredActivity.getActivity().getId())
        .collect(Collectors.toSet())
        .containsAll(activityIds)) {
      throw new DeclaredActivityNotFoundException();
    }

    declaredActivityRepository.removeAllFromDatabase(declaredActivities);
  }

  @Override
  public DeclaredActivity finish(UUID declaredActivityId) {
    Student student = loggedInUserService.getLoggedInStudent();
    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId)
            .orElseThrow(DeclaredActivityNotFoundException::new);

    if (!declaredActivity.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }

    if (declaredActivity.getStartedAt().isEmpty()) {
      throw new DeclaredActivityHasNotStartedException();
    }

    if (declaredActivity.getFinishedAt().isPresent()) {
      throw new DeclaredActivityAlreadyFinishedException();
    }

    declaredActivity.setFinishedAt(Instant.now());

    return declaredActivityRepository.save(declaredActivity);
  }

  private static void fieldsValidation(String reflection) {
    validateOptionalTextMaxLength("reflection", reflection, REFLECTION_LENGTH);
  }

  @Override
  public void updateReflection(UUID declaredActivityId, String reflection) {
    Student student = loggedInUserService.getLoggedInStudent();
    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId)
            .orElseThrow(DeclaredActivityNotFoundException::new);
    if (!declaredActivity.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }
    fieldsValidation(reflection);
    if (declaredActivity.getFinishedAt().isPresent()) {
      throw new DeclaredActivityAlreadyFinishedException();
    }
    declaredActivity.setReflection(reflection);

    if (declaredActivity.getStartedAt().isEmpty()) {
      declaredActivity.setStartedAt(Instant.now());
    }
    declaredActivityRepository.save(declaredActivity);
  }

  @Override
  public DeclaredActivity getDeclaredActivityDetails(UUID declaredActivityId) {
    return fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);
  }

  private void validateActivityDates(LocalDate startDate, LocalDate endDate, Instant subscribedAt) {

    if ((startDate == null) != (endDate == null)) {
      throw new DeclaredActivityDatesException();
    }

    if (startDate != null) {

      validateDateOrder(startDate, endDate);

      LocalDate subscriptionDate = subscribedAt.atZone(ZoneId.systemDefault()).toLocalDate();

      if (startDate.isBefore(subscriptionDate)) {
        throw new DeclaredActivityStartDateBeforeSubscriptionException();
      }
    }
  }

  @Override
  public void updateDeclaredActivityDates(
      UUID declaredActivityId, LocalDate startDate, LocalDate endDate) {

    var student = loggedInUserService.getLoggedInStudent();
    log.debug("Authenticated student id: {}", student.getId());

    log.debug("Fetching DeclaredActivity with id: {}", declaredActivityId);
    var declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId)
            .orElseThrow(
                () ->
                    new DeclaredActivityNotFoundException(
                        "DeclaredActivity not found with id: " + declaredActivityId));
    if (!declaredActivity.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }

    validateActivityDates(startDate, endDate, declaredActivity.getCreatedAt());

    declaredActivity.setStartDate(startDate);
    declaredActivity.setEndDate(endDate);

    declaredActivityRepository.save(declaredActivity);
  }

  @Override
  public PagedResult<DeclaredActivity> searchDeclaredActivity(
      String keyword, PageCriteria pageCriteria) {
    var student = loggedInUserService.getLoggedInStudent();
    var graph = FetchGraph.init().fetch("activity");
    return declaredActivityRepository.findAllByStudent(student, keyword, pageCriteria, graph);
  }

  @Override
  public DeclaredActivityAssociationsData associateActivityWithTraces(
      UUID declaredActivityId, List<UUID> traceIds) {
    Student student = loggedInUserService.getLoggedInStudent();
    fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);
    var traces = traceService.findAllTracesById(traceIds);

    if (!new HashSet<>(traces.stream().map(Trace::getId).toList()).containsAll(traceIds)) {
      throw new TraceNotFoundException();
    }

    if (!traces.stream().allMatch(trace -> trace.getUser().equals(student.getUser()))) {
      throw new UserNotAuthorizedException();
    }

    associationService.createAll(
        traceIds.stream()
            .map(
                traceId ->
                    new AssociationData(
                        declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE))
            .toList());

    return getDeclaredActivityAssociations(declaredActivityId);
  }

  @Override
  public DeclaredActivityAssociationsData associateActivityWithDeclaredSkills(
      UUID declaredActivityId, List<UUID> declaredSkillIds) {
    Student student = loggedInUserService.getLoggedInStudent();
    fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);
    var declaredSkills =
        declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(declaredSkillIds);

    if (!new HashSet<>(declaredSkills.stream().map(DeclaredSkillProgress::getId).toList())
        .containsAll(declaredSkillIds)) {
      throw new DeclaredSkillProgressNotFoundException();
    }

    if (!declaredSkills.stream().allMatch(skill -> skill.getStudent().equals(student))) {
      throw new UserNotAuthorizedException();
    }

    associationService.createAll(
        declaredSkillIds.stream()
            .map(
                skillId ->
                    new AssociationData(
                        declaredActivityId,
                        skillId,
                        EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL))
            .toList());

    return getDeclaredActivityAssociations(declaredActivityId);
  }

  @Override
  public PagedResult<TraceAssociationSearchInfoData> searchTracesForAssociation(
      UUID declaredActivityId, String keyword, PageCriteria pageCriteria, Boolean isAssociated) {
    fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);

    var alreadyAssociatedTraceIds = getAlreadyAssociatedTraceIds(declaredActivityId);

    var tracePagedResult =
        traceService.getTracesView(
            keyword, new TraceFilter(isAssociated, null, null, null), null, pageCriteria);

    var mappedContent =
        tracePagedResult.content().stream()
            .map(
                trace ->
                    new TraceAssociationSearchInfoData(
                        trace.getId(),
                        trace.getTitle(),
                        alreadyAssociatedTraceIds.contains(trace.getId())))
            .toList();

    return new PagedResult<>(mappedContent, tracePagedResult.pageInfo());
  }

  @Override
  public List<DeclaredActivity> findAllDeclaredActivitiesByIds(List<UUID> ids) {
    return declaredActivityRepository.findAllById(ids);
  }

  @Override
  public List<DeclaredActivity> findAllNotCompletedActivitiesByIds(List<UUID> ids) {
    var graph = FetchGraph.init().fetch("activity");
    return declaredActivityRepository.findAllNotCompletedActivitiesByIds(ids, graph);
  }

  @Override
  public DeclaredActivityAssociationsData getDeclaredActivityAssociations(UUID declaredActivityId) {
    DeclaredActivity declaredActivity =
        fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);

    var associations =
        associationService.getAllOf(
            declaredActivity.getId(),
            DeclaredActivity.class,
            List.of(
                EAssociationType.DECLARED_ACTIVITY_TRACE,
                EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL));

    var traces =
        traceService.findAllTracesById(
            associations.stream()
                .filter(a -> a.getAssociationType() == EAssociationType.DECLARED_ACTIVITY_TRACE)
                .map(Association::getId2)
                .toList());

    var declaredSkills =
        declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
            associations.stream()
                .filter(
                    a ->
                        a.getAssociationType() == EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL)
                .map(Association::getId2)
                .toList());

    return new DeclaredActivityAssociationsData(
        associations.stream()
            .filter(a -> a.getAssociationType() == EAssociationType.DECLARED_ACTIVITY_TRACE)
            .map(
                a ->
                    new TraceAssociationData(
                        a.getId(),
                        traces.stream()
                            .filter(t -> t.getId().equals(a.getId2()))
                            .findAny()
                            .orElseThrow(TraceNotFoundException::new)))
            .toList(),
        associations.stream()
            .filter(
                a -> a.getAssociationType() == EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL)
            .map(
                a ->
                    new DeclaredSkillAssociationData(
                        a.getId(),
                        declaredSkills.stream()
                            .filter(s -> s.getId().equals(a.getId2()))
                            .findAny()
                            .orElseThrow(DeclaredSkillProgressNotFoundException::new)))
            .toList());
  }

  @Override
  public void deleteAssociations(UUID declaredActivityId, List<UUID> idsToDelete) {
    DeclaredActivity declaredActivity =
        fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);

    var associatedTracesIds =
        associationService
            .getAllOf(
                declaredActivity.getId(),
                DeclaredActivity.class,
                List.of(EAssociationType.DECLARED_ACTIVITY_TRACE))
            .stream()
            .map(Association::getId)
            .toList();

    if (!new HashSet<>(associatedTracesIds).containsAll(idsToDelete)) {
      throw new UserNotAuthorizedException();
    }

    associationService.deleteAllByIds(idsToDelete);
  }

  private DeclaredActivity fetchActivityAndCheckLoggedInStudentAuthorization(
      UUID declaredActivityId) {
    Student student = loggedInUserService.getLoggedInStudent();
    var graph = FetchGraph.init().fetch("activity").add("student").fetch("user");

    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId, graph)
            .orElseThrow(DeclaredActivityNotFoundException::new);

    if (!declaredActivity.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }
    return declaredActivity;
  }

  private Set<UUID> getAlreadyAssociatedTraceIds(UUID declaredActivityId) {
    return associationService
        .getAllOf(
            declaredActivityId,
            DeclaredActivity.class,
            List.of(EAssociationType.DECLARED_ACTIVITY_TRACE))
        .stream()
        .map(Association::getId2)
        .collect(Collectors.toSet());
  }
}
