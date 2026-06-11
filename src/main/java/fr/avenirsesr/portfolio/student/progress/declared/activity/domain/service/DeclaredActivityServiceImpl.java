package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateDateOrder;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateOptionalTextMaxLength;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.association.domain.exception.MaximumAssociationReachedException;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.association.domain.utils.AssociationUtils;
import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.notification.domain.model.notification.AskForFeedbackNotification;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityAssociationsData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityDetailsData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.*;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.FeedbackRepository;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.trace.domain.data.TraceAssociationData;
import fr.avenirsesr.portfolio.trace.domain.data.TraceViewData;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
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
  private final TraceService traceService;
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final AssociationService associationService;
  private final AssociationSearchHelper associationSearchHelper;
  private final LoggedInUserService loggedInUserService;
  private final FeedbackRepository feedbackRepository;
  private final NotificationService notificationService;

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

    associationService.deleteAllOf(activityIds, DeclaredActivity.class);

    declaredActivityRepository.removeAllFromDatabase(declaredActivities);
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
    if (declaredActivity.getFinishedAt().isPresent()) {
      throw new DeclaredActivityAlreadyFinishedException();
    }
    if (!declaredActivity.getActivity().isEnableReflection()) {
      throw new UserNotAuthorizedException();
    }
    validateOptionalTextMaxLength("reflection", reflection, RICH_TEXT_LENGTH);

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
    List<Feedback> feedbacks = feedbackRepository.findAllByDeclaredActivityId(declaredActivityId);
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
    DeclaredActivity declaredActivity =
        fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);
    var traces = traceService.findAllTracesById(traceIds);

    if (!new HashSet<>(traces.stream().map(Trace::getId).toList()).containsAll(traceIds)) {
      throw new TraceNotFoundException();
    }

    if (!traces.stream().allMatch(trace -> trace.getUser().equals(student.getUser()))) {
      throw new UserNotAuthorizedException();
    }

    var traceAssociations =
        associationService.getAllOf(
            declaredActivityId,
            DeclaredActivity.class,
            List.of(EAssociationType.DECLARED_ACTIVITY_TRACE));
    var activity = declaredActivity.getActivity();
    if (activity.getTraceAllowedAssociations() != -1
        && activity.getTraceAllowedAssociations() >= traceAssociations.size()) {
      throw new MaximumAssociationReachedException();
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
  public PagedResult<AssociationSearchResultData> searchTracesForAssociation(
      UUID declaredActivityId, String keyword, PageCriteria pageCriteria, Boolean isAssociated) {
    fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);
    var associationType = EAssociationType.DECLARED_ACTIVITY_TRACE;
    return associationSearchHelper.searchForAssociation(
        declaredActivityId,
        DeclaredActivity.class,
        associationType,
        associationType.idExtractorFor(Trace.class),
        traceService.getTracesView(
            keyword, new TraceFilter(isAssociated, null, null, null), null, pageCriteria),
        TraceViewData::id,
        TraceViewData::title,
        null,
        trace -> false);
  }

  @Override
  public PagedResult<AssociationSearchResultData> searchDeclaredActivitiesForAssociation(
      UUID excludeAssociatedWithElementId,
      EAssociationContextType contextType,
      String keyword,
      PageCriteria pageCriteria) {
    var activities = searchDeclaredActivity(keyword, pageCriteria);

    if (contextType == null) {
      return associationSearchHelper.searchForAssociation(
          null,
          null,
          null,
          null,
          activities,
          AvenirsBaseModel::getId,
          da -> da.getActivity().getTitle(),
          da -> da.getActivity().getThematic().name(),
          da -> da.getFinishedAt().isPresent());
    }

    EAssociationType associationType = getAssociationType(contextType);

    return associationSearchHelper.searchForAssociation(
        excludeAssociatedWithElementId,
        contextType.toClass(),
        associationType,
        associationType.idExtractorFor(DeclaredActivity.class),
        activities,
        AvenirsBaseModel::getId,
        da -> da.getActivity().getTitle(),
        da -> da.getActivity().getThematic().name(),
        da -> da.getFinishedAt().isPresent());
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
            AssociationUtils.getIdsOf(
                associations, EAssociationType.DECLARED_ACTIVITY_TRACE, Trace.class));

    var declaredSkills =
        declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
            AssociationUtils.getIdsOf(
                associations,
                EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                DeclaredSkillProgress.class));

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
    var graph =
        FetchGraph.init().add("student").fetch("user").root().add("activity").fetch("author");

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
  public Feedback createFeedback(UUID declaredActivityId) {
    DeclaredActivity declaredActivity =
        fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);

    validateOptionalTextMaxLength("reflexion", declaredActivity.getReflection(), RICH_TEXT_LENGTH);

    var allAssociations =
        associationService.getAllOf(
            declaredActivityId,
            DeclaredActivity.class,
            List.of(
                EAssociationType.DECLARED_ACTIVITY_TRACE,
                EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL));

    var traceIds =
        AssociationUtils.getIdsOf(
            allAssociations, EAssociationType.DECLARED_ACTIVITY_TRACE, Trace.class);

    var declaredSkillsIds =
        AssociationUtils.getIdsOf(
            allAssociations,
            EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
            DeclaredSkillProgress.class);

    List<Trace> traces = traceService.findAllTracesById(traceIds);
    List<DeclaredSkillProgress> declaredSkills =
        declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(declaredSkillsIds);

    List<Feedback> existingFeedbacks =
        feedbackRepository.findAllByDeclaredActivityId(declaredActivityId);

    if (!existingFeedbacks.isEmpty()) {
      Feedback lastFeedback = existingFeedbacks.getLast();
      switch (lastFeedback.getStatus()) {
        case IN_PROCESS -> throw new FeedbackInProcessException();
        case NEW -> {
          lastFeedback.setReflexion(declaredActivity.getReflection());
          lastFeedback.setAssociatedTraces(traces);
          lastFeedback.setAssociatedDeclaredSkills(declaredSkills);
          return feedbackRepository.save(lastFeedback);
        }
        case SUBMITTED -> {
          if (existingFeedbacks.size()
              >= declaredActivity.getActivity().getFeedbackAllowedIterations()) {
            throw new FeedbackMaximumIterationReachedException();
          }
        }
      }
    }

    int iteration = existingFeedbacks.size() + 1;
    Feedback feedback =
        Feedback.create(
            declaredActivity, declaredActivity.getReflection(), traces, declaredSkills, iteration);
    var savedFeedback = feedbackRepository.save(feedback);

    var author = savedFeedback.getDeclaredActivity().getActivity().getAuthor().getUser();
    notificationService.notify(new AskForFeedbackNotification(author, savedFeedback));

    return savedFeedback;
  }

  @Override
  public Feedback getFeedbackDetails(UUID feedbackId) {
    Feedback feedback =
        feedbackRepository.findById(feedbackId).orElseThrow(FeedbackNotFoundException::new);

    UUID loggedInUserId = loggedInUserService.getLoggedInUser().getId();
    UUID studentId = feedback.getDeclaredActivity().getStudent().getId();
    UUID authorId = feedback.getDeclaredActivity().getActivity().getAuthor().getId();

    if (!loggedInUserId.equals(studentId) || !loggedInUserId.equals(authorId)) {
      throw new UserNotAuthorizedException();
    }

    return feedback;
  }

  @Override
  public PagedResult<Feedback> getStaffFeedbacks(
      EFeedbackStatus statusFilter, UUID activityId, PageCriteria pageCriteria) {
    var staff = loggedInUserService.getLoggedInStaff();
    return feedbackRepository.findByStaff(staff.getId(), statusFilter, activityId, pageCriteria);
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

  private EAssociationType getAssociationType(EAssociationContextType contextType) {
    return switch (contextType) {
      case TRACE -> EAssociationType.DECLARED_ACTIVITY_TRACE;
      case DECLARED_SKILL -> EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL;
      case DECLARED_ACTIVITY, DECLARED_EXPERIENCE -> throw new UnsupportedOperationException();
    };
  }

  private boolean isSubmittedOrFinished(
      DeclaredActivity declaredActivity, boolean hasActiveFeedback) {
    EDeclaredActivityStatus status = getDeclaredActivityStatus(declaredActivity, hasActiveFeedback);

    return status == EDeclaredActivityStatus.SUBMITTED
        || status == EDeclaredActivityStatus.COMPLETED;
  }
}
