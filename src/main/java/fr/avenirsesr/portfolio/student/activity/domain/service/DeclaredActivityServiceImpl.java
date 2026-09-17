package fr.avenirsesr.portfolio.student.activity.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateDateOrder;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateOptionalEnrichedTextMaxLength;

import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityUnpublishedException;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssociationsData;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityDetailsData;
import fr.avenirsesr.portfolio.student.activity.domain.data.FeedbackData;
import fr.avenirsesr.portfolio.student.activity.domain.exception.*;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.FeedbackService;
import fr.avenirsesr.portfolio.student.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.activity.domain.port.output.repository.FeedbackRepository;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.exception.MaximumAssociationReachedException;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class DeclaredActivityServiceImpl implements DeclaredActivityService {
  private final DeclaredActivityRepository declaredActivityRepository;
  private final ActivityService activityService;
  private final AssociationService associationService;
  private final LoggedInUserService loggedInUserService;
  private final FeedbackRepository feedbackRepository;
  private final FeedbackService feedbackService;

  @Override
  public PagedResult<DeclaredActivity> getDeclaredActivities(PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    var graph = FetchGraph.init().add("activity").fetch("author");

    return declaredActivityRepository.findStudentActivitiesByProgressAndDate(
        student, pageCriteria, graph);
  }

  @Override
  public List<DeclaredActivity> getAllDeclaredActivitiesOf(Student student) {
    var graph = FetchGraph.init().add("activity").fetch("author");
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

    if (activity.getStatus() == EActivityStatus.UNPUBLISHED) {
      throw new ActivityUnpublishedException();
    }

    validateActivityDates(startDate, endDate, Instant.now());

    var existingDeclaredActivity = declaredActivityRepository.findByActivity(student, activity);
    if (existingDeclaredActivity.filter(da -> !da.isUnsubscribed()).isPresent()) {
      throw new DeclaredActivityAlreadyExistException();
    }

    var declaredActivity =
        existingDeclaredActivity.orElse(
            DeclaredActivity.create(
                declaredActivityId, student, activity, null, null, startDate, endDate, null));

    declaredActivity.setUnsubscribedAt(null);
    declaredActivity.setStartDate(startDate);
    declaredActivity.setEndDate(endDate);

    return declaredActivityRepository.save(declaredActivity);
  }

  @Override
  public void unsubscribeMultiple(List<UUID> activityIds) {
    Student student = loggedInUserService.getLoggedInStudent();

    List<DeclaredActivity> declaredActivities =
        declaredActivityRepository.findAllByActivityIdAndStudent(
            activityIds,
            student,
            FetchGraph.init().add("activity").fetch("author").root().add("student").fetch("user"));

    if (!declaredActivities.stream()
        .map(declaredActivity -> declaredActivity.getActivity().getId())
        .collect(Collectors.toSet())
        .containsAll(activityIds)) {
      throw new DeclaredActivityNotFoundException();
    }

    var activitiesToUnsubscribe =
        declaredActivities.stream()
            .filter(declaredActivity -> !declaredActivity.isUnsubscribed())
            .toList();

    if (activitiesToUnsubscribe.isEmpty()) {
      return;
    }

    feedbackService.deletePendingFeedbacks(
        activitiesToUnsubscribe.stream().map(DeclaredActivity::getId).toList());

    var unsubscribedAt = Instant.now();
    activitiesToUnsubscribe.forEach(
        declaredActivity -> declaredActivity.unsubscribe(unsubscribedAt));

    declaredActivityRepository.saveAll(activitiesToUnsubscribe);
  }

  @Override
  public void finish(UUID declaredActivityId) {
    Student student = loggedInUserService.getLoggedInStudent();
    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId)
            .orElseThrow(DeclaredActivityNotFoundException::new);

    if (!declaredActivity.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }

    if (declaredActivity.isUnsubscribed()) {
      throw new DeclaredActivityUnsubscribedException();
    }

    if (declaredActivity.getStartedAt().isEmpty()) {
      throw new DeclaredActivityHasNotStartedException();
    }

    if (declaredActivity.getFinishedAt().isPresent()) {
      throw new DeclaredActivityAlreadyFinishedException();
    }

    declaredActivity.setFinishedAt(Instant.now());

    declaredActivityRepository.save(declaredActivity);
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
    if (declaredActivity.isUnsubscribed()) {
      throw new DeclaredActivityUnsubscribedException();
    }
    if (declaredActivity.getFinishedAt().isPresent()) {
      throw new DeclaredActivityAlreadyFinishedException();
    }
    if (!declaredActivity.getActivity().isEnableReflection()) {
      throw new UserNotAuthorizedException();
    }
    validateOptionalEnrichedTextMaxLength("reflection", reflection, RICH_TEXT_LENGTH);

    declaredActivity.setReflection(reflection);

    if (declaredActivity.getStartedAt().isEmpty()) {
      declaredActivity.setStartedAt(Instant.now());
    }
    declaredActivityRepository.save(declaredActivity);
  }

  @Override
  public DeclaredActivityDetailsData getDeclaredActivityDetails(UUID declaredActivityId) {
    DeclaredActivity declaredActivity =
        fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);
    List<FeedbackData> feedbacks =
        feedbackRepository.findAllByDeclaredActivityId(declaredActivityId).stream()
            .map(
                feedback ->
                    feedbackService.getStudentFeedbackDetails(
                        declaredActivity.getStudent().getUser(), feedback))
            .toList();
    return new DeclaredActivityDetailsData(declaredActivity, feedbacks);
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
  public void updateDeclaredActivity(
      UUID declaredActivityId, LocalDate startDate, LocalDate endDate, Boolean valorized) {

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

    if (declaredActivity.isUnsubscribed()) {
      throw new DeclaredActivityUnsubscribedException();
    }

    if (startDate != null || endDate != null) {
      validateActivityDates(startDate, endDate, declaredActivity.getCreatedAt());
      declaredActivity.setStartDate(startDate);
      declaredActivity.setEndDate(endDate);
    }

    if (valorized != null) {
      declaredActivity.setValorized(valorized);
    }

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
  public DeclaredActivityAssociationsData associate(
      UUID declaredActivityId, List<UUID> associatedIds, EAssociationType associationType) {
    DeclaredActivity declaredActivity =
        fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);

    if (declaredActivity.isUnsubscribed()) {
      throw new DeclaredActivityUnsubscribedException();
    }

    if (associationType == EAssociationType.DECLARED_ACTIVITY_TRACE) {
      checkMaximumAllowedTraceAssociations(declaredActivity, associatedIds);
    }

    associationService.associate(
        declaredActivity.getId(), DeclaredActivity.class, associatedIds, associationType);

    return getDeclaredActivityAssociations(declaredActivityId);
  }

  private void checkMaximumAllowedTraceAssociations(
      DeclaredActivity declaredActivity, List<UUID> traceIds) {
    var allowedAssociations = declaredActivity.getActivity().getTraceAllowedAssociations();

    if (allowedAssociations == -1) {
      return;
    }

    var traceAssociations =
        associationService.getAllOf(
            declaredActivity.getId(),
            DeclaredActivity.class,
            List.of(EAssociationType.DECLARED_ACTIVITY_TRACE));

    if (traceAssociations.size() + traceIds.size() > allowedAssociations) {
      throw new MaximumAssociationReachedException();
    }
  }

  @Override
  public PagedResult<AssociationSearchResultData> searchForAssociation(
      UUID declaredActivityId,
      EAssociationContextType contextType,
      String keyword,
      PageCriteria pageCriteria) {
    fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);

    return associationService.searchForAssociation(
        declaredActivityId, DeclaredActivity.class, contextType, keyword, pageCriteria);
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

    var associatedElements =
        associationService.getAllAssociatedElementsOf(
            declaredActivity.getId(), DeclaredActivity.class);

    return new DeclaredActivityAssociationsData(
        associatedElements.traceAssociations(), associatedElements.declaredSkillAssociations());
  }

  @Override
  public void deleteAssociations(UUID declaredActivityId, List<UUID> idsToDelete) {
    DeclaredActivity declaredActivity =
        fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);
    if (declaredActivity.isUnsubscribed()) {
      throw new DeclaredActivityUnsubscribedException();
    }

    associationService.unassociate(declaredActivity.getId(), DeclaredActivity.class, idsToDelete);
  }

  @Override
  public DeclaredActivity fetchActivityAndCheckLoggedInStudentAuthorization(
      UUID declaredActivityId) {
    Student student = loggedInUserService.getLoggedInStudent();
    var graph =
        FetchGraph.init()
            .add("student")
            .fetch("user")
            .root()
            .add("activity")
            .fetch("author")
            .fetch("files");

    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId, graph)
            .orElseThrow(DeclaredActivityNotFoundException::new);

    if (!declaredActivity.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }
    return declaredActivity;
  }

  @Override
  public boolean areDeclaredActivitiesUnlocked(List<UUID> declaredActivityIds) {
    if (declaredActivityIds.isEmpty()) {
      return true;
    }

    var declaredActivities = findAllDeclaredActivitiesByIds(declaredActivityIds);
    var declaredActivityIdsWithFeedback =
        findFeedbackPresenceByDeclaredActivityId(declaredActivityIds);

    return declaredActivities.stream()
        .noneMatch(
            declaredActivity ->
                isSubmittedOrFinished(
                    declaredActivity,
                    declaredActivityIdsWithFeedback.contains(declaredActivity.getId())));
  }

  @Override
  public void checkDeclaredActivitiesUnlocked(List<UUID> declaredActivityIds) {
    if (!areDeclaredActivitiesUnlocked(declaredActivityIds)) {
      throw new DeclaredActivityLockedException();
    }
  }

  @Override
  public EDeclaredActivityStatus getDeclaredActivityStatus(DeclaredActivity declaredActivity) {
    boolean hasActiveFeedback =
        feedbackRepository
            .findDeclaredActivityIdsHavingActiveFeedbacks(List.of(declaredActivity.getId()))
            .contains(declaredActivity.getId());

    return getDeclaredActivityStatus(declaredActivity, hasActiveFeedback);
  }

  @Override
  public Map<DeclaredActivity, EDeclaredActivityStatus> getDeclaredActivityStatus(
      List<DeclaredActivity> declaredActivities) {
    var declaredActivityIds = declaredActivities.stream().map(DeclaredActivity::getId).toList();

    var idsWithFeedback =
        new HashSet<>(findFeedbackPresenceByDeclaredActivityId(declaredActivityIds));

    return declaredActivities.stream()
        .collect(
            Collectors.toMap(
                Function.identity(),
                declaredActivity ->
                    getDeclaredActivityStatus(
                        declaredActivity, idsWithFeedback.contains(declaredActivity.getId()))));
  }

  private EDeclaredActivityStatus getDeclaredActivityStatus(
      DeclaredActivity declaredActivity, boolean hasActiveFeedback) {
    if (declaredActivity.isUnsubscribed()) {
      return EDeclaredActivityStatus.UNSUBSCRIBED;
    }

    if (declaredActivity.getFinishedAt().isPresent()) {
      return EDeclaredActivityStatus.COMPLETED;
    }

    if (hasActiveFeedback) {
      return EDeclaredActivityStatus.SUBMITTED;
    }

    if (declaredActivity.getStartedAt().isPresent()
        && declaredActivity.getStartedAt().get().isBefore(Instant.now())) {
      return EDeclaredActivityStatus.IN_PROGRESS;
    }

    return EDeclaredActivityStatus.SUBSCRIBED;
  }

  private List<UUID> findFeedbackPresenceByDeclaredActivityId(List<UUID> declaredActivityIds) {
    if (declaredActivityIds.isEmpty()) {
      return List.of();
    }

    return feedbackRepository.findDeclaredActivityIdsHavingActiveFeedbacks(declaredActivityIds);
  }

  @Override
  public int countEnrolledStudents(Activity activity) {
    return declaredActivityRepository.countEnrolledByActivity(activity);
  }

  @Override
  public int countUnsubscriptionsSince(Activity activity, Instant since) {
    return declaredActivityRepository.countUnsubscribedByActivitySince(activity, since);
  }

  @Override
  public List<DeclaredActivity> getEnrolledStudents(Activity activity) {
    var graph =
        FetchGraph.init().add("student").fetch("user").root().add("activity").fetch("author");
    return declaredActivityRepository.findAllEnrolledByActivity(activity, graph);
  }

  @Override
  public boolean isEnrolled(Activity activity, Student student) {
    return declaredActivityRepository
        .findByActivity(student, activity)
        .filter(declaredActivity -> !declaredActivity.isUnsubscribed())
        .isPresent();
  }

  private boolean isSubmittedOrFinished(
      DeclaredActivity declaredActivity, boolean hasActiveFeedback) {
    EDeclaredActivityStatus status = getDeclaredActivityStatus(declaredActivity, hasActiveFeedback);

    return status == EDeclaredActivityStatus.SUBMITTED
        || status == EDeclaredActivityStatus.COMPLETED;
  }
}
