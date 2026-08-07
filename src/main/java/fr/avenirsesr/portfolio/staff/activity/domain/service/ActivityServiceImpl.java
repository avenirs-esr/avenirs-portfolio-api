package fr.avenirsesr.portfolio.staff.activity.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.*;
import static fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityUpdatableField.*;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.file.domain.mapper.FileDataMapper;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.FileDownload;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import fr.avenirsesr.portfolio.notification.domain.model.notification.ActivityUpdatedNotification;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityDatesException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityDraftNotFoundException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityUnpublishedException;
import fr.avenirsesr.portfolio.staff.activity.domain.mapper.ActivityPresentationDataMapper;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityUpdatableField;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.StaffActivityOverviewRepository;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {

  private static final Duration DURATION_FOR_LATEST = Duration.ofDays(90);
  private static final EnumSet<EFileType> ALLOWED_DRAFT_FILE_TYPES =
      EnumSet.of(
          EFileType.PDF,
          EFileType.DOC,
          EFileType.DOCX,
          EFileType.ODT,
          EFileType.JPEG,
          EFileType.PNG);
  private final ActivityRepository activityRepository;
  private final ActivityDraftRepository activityDraftRepository;
  private final DeclaredActivityService declaredActivityService;
  private final LoggedInUserService loggedInUserService;
  private final StaffActivityOverviewRepository staffActivityOverviewRepository;
  private final NotificationService notificationService;
  private final FileResourceService fileResourceService;
  private final StudentRepository studentRepository;

  @Override
  public Activity create(
      UUID id,
      Staff author,
      String title,
      EActivityThematic thematic,
      String summary,
      String description,
      String recommendedCompletionContexts,
      LocalDate startDate,
      LocalDate endDate,
      boolean enableReflection,
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      List<String> links) {
    requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
    requireNotNull("thematic", thematic);
    requireNotBlankAndMaxLength("summary", summary, SUMMARY_LENGTH);
    requireNotBlankAndEnrichedMaxLength("description", description, RICH_DESCRIPTION_LENGTH);
    requireNotBlankAndMaxLength(
        "recommendedCompletionContexts",
        recommendedCompletionContexts,
        ACTIVITY_RECOMMENDED_COMPLETION_CONTEXTS);
    if ((startDate == null) != (endDate == null)) {
      throw new ActivityDatesException();
    }

    if (startDate != null) {
      validateDateOrder(startDate, endDate);
    }

    var activity =
        Activity.create(
            id,
            author,
            title,
            thematic,
            summary,
            description,
            recommendedCompletionContexts,
            startDate,
            endDate,
            enableReflection,
            traceAllowedAssociations,
            feedbackAllowedIterations,
            null,
            links,
            List.of());
    activityRepository.save(activity);
    return activity;
  }

  @Override
  public Activity publish(UUID activityDraftId) {
    var staff = loggedInUserService.getLoggedInStaff();
    var draft =
        activityDraftRepository
            .findById(activityDraftId)
            .orElseThrow(ActivityDraftNotFoundException::new);
    if (!draft.getAuthor().equals(staff)) {
      throw new UserNotAuthorizedException();
    }

    if (draft.getSummary().isEmpty()) {
      throw new FieldValidationException(
          EErrorCode.NOT_BLANK, "summary must be defined to publish an activity");
    }

    if (draft.getDescription().isEmpty()) {
      throw new FieldValidationException(
          EErrorCode.NOT_BLANK, "description must be defined to publish an activity");
    }

    var publishedActivity = activityRepository.findById(activityDraftId);

    Activity activity =
        publishedActivity.orElse(
            Activity.create(
                draft.getId(),
                draft.getAuthor(),
                draft.getTitle(),
                draft.getThematic(),
                draft.getSummary().orElseThrow(),
                draft.getDescription().orElseThrow(),
                draft.getRecommendedCompletionContexts().orElse(null),
                draft.getStartDate().orElse(null),
                draft.getEndDate().orElse(null),
                draft.isEnableReflection(),
                draft.getTraceAllowedAssociations(),
                draft.getFeedbackAllowedIterations(),
                draft.getBanner().orElse(null),
                draft.getLinks(),
                draft.getFiles()));

    if (publishedActivity.isPresent()) {
      var enrolledDeclaredActivities = declaredActivityService.getEnrolledStudents(activity);
      var updatedFields = updateActivity(activity, draft, !enrolledDeclaredActivities.isEmpty());
      activity.setStatus(EActivityStatus.PUBLISHED);
      notifyActivityUpdated(updatedFields, enrolledDeclaredActivities);
    }

    var savedActivity = activityRepository.save(activity);

    activityDraftRepository.removeFromDatabase(draft);
    return savedActivity;
  }

  private record FieldSync<T>(
      EActivityUpdatableField field, T currentValue, T newValue, Consumer<T> setter) {
    boolean applyIfChanged() {
      if (Objects.equals(currentValue, newValue)) {
        return false;
      }
      setter.accept(newValue);
      return true;
    }
  }

  private List<EActivityUpdatableField> updateActivity(
      Activity activity, ActivityDraft draft, boolean hasEnrolledStudents) {
    var syncs =
        List.of(
            new FieldSync<>(
                ACTIVITY_TITLE, activity.getTitle(), draft.getTitle(), activity::setTitle),
            new FieldSync<>(
                SUMMARY,
                activity.getSummary(),
                draft.getSummary().orElse(null),
                activity::setSummary),
            new FieldSync<>(
                DESCRIPTION,
                activity.getDescription(),
                draft.getDescription().orElse(null),
                activity::setDescription),
            new FieldSync<>(
                RECOMMENDED_COMPLETION_CONTEXTS,
                activity.getRecommendedCompletionContexts().orElse(null),
                draft.getRecommendedCompletionContexts().orElse(null),
                activity::setRecommendedCompletionContexts),
            new FieldSync<>(
                THEMATIC, activity.getThematic(), draft.getThematic(), activity::setThematic),
            new FieldSync<>(
                BANNER,
                activity.getBanner().orElse(null),
                draft.getBanner().orElse(null),
                activity::setBanner),
            new FieldSync<>(
                FILES_AND_LINKS,
                new HashSet<>(activity.getLinks()),
                new HashSet<>(draft.getLinks()),
                links -> activity.setLinks(links.stream().toList())),
            new FieldSync<>(
                FILES_AND_LINKS, activity.getFiles(), draft.getFiles(), activity::setFiles));

    var updatedFields =
        syncs.stream().filter(FieldSync::applyIfChanged).map(FieldSync::field).distinct().toList();

    activity.setStartDate(draft.getStartDate().orElse(null));
    activity.setEndDate(draft.getEndDate().orElse(null));

    if (!hasEnrolledStudents) {
      activity.setTraceAllowedAssociations(draft.getTraceAllowedAssociations());
      activity.setFeedbackAllowedIterations(draft.getFeedbackAllowedIterations());
      activity.setEnableReflection(draft.isEnableReflection());
    }

    return updatedFields;
  }

  private void notifyActivityUpdated(
      List<EActivityUpdatableField> updatedFields, List<DeclaredActivity> declaredActivities) {
    if (declaredActivities.isEmpty() || updatedFields.isEmpty()) {
      return;
    }
    notificationService.notifyAll(
        declaredActivities.stream()
            .map(
                declaredActivity ->
                    new ActivityUpdatedNotification(
                        declaredActivity.getStudent().getUser(), declaredActivity, updatedFields))
            .toList());
  }

  @Override
  public Activity unpublish(UUID activityId) {
    var staff = loggedInUserService.getLoggedInStaff();
    var activity =
        activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
    if (!activity.getAuthor().equals(staff)) {
      throw new UserNotAuthorizedException();
    }
    if (activity.getStatus() == EActivityStatus.UNPUBLISHED) {
      throw new ActivityUnpublishedException();
    }
    activity.setStatus(EActivityStatus.UNPUBLISHED);
    return activityRepository.save(activity);
  }

  @Override
  public void deleteDraft(UUID activityDraftId) {
    var staff = loggedInUserService.getLoggedInStaff();
    var draft =
        activityDraftRepository
            .findById(activityDraftId)
            .orElseThrow(ActivityDraftNotFoundException::new);
    if (!draft.getAuthor().equals(staff)) {
      throw new UserNotAuthorizedException();
    }

    activityDraftRepository.removeFromDatabase(draft);
    log.info("Deleted activity draft with id: {}", activityDraftId);
  }

  @Override
  public Activity getActivityById(UUID id) {
    return activityRepository.findById(id).orElseThrow(ActivityNotFoundException::new);
  }

  @Override
  public ActivityDraft getActivityDraftById(UUID id) {
    var staff = loggedInUserService.getLoggedInStaff();
    var draft =
        activityDraftRepository.findById(id).orElseThrow(ActivityDraftNotFoundException::new);
    if (!draft.getAuthor().equals(staff)) {
      throw new UserNotAuthorizedException();
    }
    return draft;
  }

  @Override
  public ActivityPresentationData getActivityPresentation(EActivityStatus activityStatus, UUID id) {
    return switch (activityStatus) {
      case PUBLISHED, UNPUBLISHED -> {
        Activity activity =
            activityRepository.findById(id).orElseThrow(ActivityNotFoundException::new);
        yield ActivityPresentationDataMapper.toData(
            activity,
            declaredActivityService
                .getByActivity(activity)
                .map(DeclaredActivity::getId)
                .orElse(null),
            FileDataMapper.mapFileData(
                activity.getBanner(), FileStorageConstants.DEFAULT_COVER_FILE_URL));
      }
      case DRAFT -> {
        var draft =
            activityDraftRepository.findById(id).orElseThrow(ActivityDraftNotFoundException::new);
        yield ActivityPresentationDataMapper.toData(
            draft,
            FileDataMapper.mapFileData(
                draft.getBanner(), FileStorageConstants.DEFAULT_COVER_FILE_URL));
      }
    };
  }

  @Override
  public Map<EActivityThematic, List<Activity>> getActivityNavigation() {
    return activityRepository.findAll().stream()
        .filter(a -> a.getStatus() != EActivityStatus.UNPUBLISHED)
        .collect(
            Collectors.groupingBy(
                Activity::getThematic,
                () -> new EnumMap<>(EActivityThematic.class),
                Collectors.toList()));
  }

  @Override
  public PagedResult<ActivityStaffOverviewData> staffActivityWorkingSpace(
      PageCriteria pageCriteria, EActivityStatus activityStatus) {
    var staff = loggedInUserService.getLoggedInStaff();
    var pagedActivities =
        staffActivityOverviewRepository.findAllByAuthorAndStatus(
            staff, activityStatus, pageCriteria);

    return new PagedResult<>(
        pagedActivities.content().stream()
            .map(
                a ->
                    new ActivityStaffOverviewData(
                        a.activityId(),
                        a.title(),
                        a.thematic(),
                        a.author(),
                        a.activityStatus(),
                        a.updatedAt()))
            .toList(),
        pagedActivities.pageInfo());
  }

  @Override
  public PagedResult<ActivityStaffOverviewData> staffActivityLibrary(
      EActivityThematic thematic, PageCriteria pageCriteria) {
    loggedInUserService.getLoggedInStaff();
    return activityRepository.findAllStaffOverview(thematic, pageCriteria);
  }

  @Override
  public PagedResult<ActivityWithStudentStatusData> activitiesView(
      EActivityThematic thematic, PageCriteria pageCriteria) {
    var pagedActivities = activityRepository.findAll(thematic, pageCriteria);
    var student = loggedInUserService.getLoggedInStudent();
    var subscribedActivities = declaredActivityService.getAllDeclaredActivitiesOf(student);
    var statusByDeclaredActivity =
        declaredActivityService.getDeclaredActivityStatus(subscribedActivities);

    return new PagedResult<>(
        pagedActivities.content().stream()
            .map(
                activity -> {
                  var declaredActivity =
                      subscribedActivities.stream()
                          .filter(a -> a.getActivity().equals(activity))
                          .findFirst();

                  return new ActivityWithStudentStatusData(
                      activity,
                      activity.getCreatedAt().isAfter(Instant.now().minus(DURATION_FOR_LATEST)),
                      declaredActivity.map(statusByDeclaredActivity::get).orElse(null));
                })
            .toList(),
        pagedActivities.pageInfo());
  }

  @Override
  public PagedResult<ActivityWithStudentStatusData> latestActivitiesView(
      PageCriteria pageCriteria) {
    var student = loggedInUserService.getLoggedInStudent();
    var subscribedActivities = declaredActivityService.getAllDeclaredActivitiesOf(student);
    var pagedActivities =
        activityRepository.findLatest(
            DURATION_FOR_LATEST,
            subscribedActivities.stream().map(DeclaredActivity::getActivity).toList(),
            pageCriteria);
    return new PagedResult<>(
        pagedActivities.content().stream()
            .map(activity -> new ActivityWithStudentStatusData(activity, true, null))
            .toList(),
        pagedActivities.pageInfo());
  }

  @Override
  public ActivityDraft createActivityDraft(String title) {
    var staff = loggedInUserService.getLoggedInStaff();
    requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
    var draft = ActivityDraft.create(title, staff);
    return activityDraftRepository.save(draft);
  }

  @Override
  public ActivityDraft updateActivityDraft(
      UUID id,
      String title,
      EActivityThematic thematic,
      String summary,
      String description,
      String recommendedCompletionContexts,
      LocalDate startDate,
      LocalDate endDate,
      Integer traceAllowedAssociations,
      Integer feedbackAllowedIterations,
      Boolean enableReflection,
      List<String> links) {
    var loggedInStaff = loggedInUserService.getLoggedInStaff();
    var draft =
        activityDraftRepository.findById(id).orElseThrow(ActivityDraftNotFoundException::new);

    if (!draft.getAuthor().equals(loggedInStaff)) {
      throw new UserNotAuthorizedException();
    }

    validateOptionalTextMaxLength("summary", summary, SUMMARY_LENGTH);
    validateOptionalTextMaxLength("description", description, RICH_DESCRIPTION_LENGTH);
    validateOptionalTextMaxLength(
        "recommendedCompletionContexts",
        recommendedCompletionContexts,
        ACTIVITY_RECOMMENDED_COMPLETION_CONTEXTS);
    if (startDate != null) {
      validateDateOrder(startDate, endDate);
    }
    if ((startDate == null) != (endDate == null)) {
      throw new ActivityDatesException();
    }

    if (title != null) {
      requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
      draft.setTitle(title);
    }
    if (thematic != null) draft.setThematic(thematic);
    if (summary != null) draft.setSummary(summary);
    if (description != null) draft.setDescription(description);
    if (recommendedCompletionContexts != null)
      draft.setRecommendedCompletionContexts(recommendedCompletionContexts);
    if (startDate != null) draft.setStartDate(startDate);
    if (endDate != null) draft.setEndDate(endDate);

    if (!hasEnrolledStudents(draft)) {
      if (traceAllowedAssociations != null)
        draft.setTraceAllowedAssociations(traceAllowedAssociations);
      if (feedbackAllowedIterations != null)
        draft.setFeedbackAllowedIterations(feedbackAllowedIterations);
      if (enableReflection != null) draft.setEnableReflection(enableReflection);
    }

    if (links != null && !links.isEmpty()) {
      links.forEach(
          link -> {
            requireNotBlankAndMaxLength("link", link, LINK_LENGTH);
            validateUrl(link);
          });
      draft.setLinks(links);
    }

    var updatedDraft = activityDraftRepository.save(draft);
    log.info("Updated activity draft with id: {}", id);
    return updatedDraft;
  }

  @Override
  public ActivityDraft createDraftFromActivity(UUID activityId) {
    var staff = loggedInUserService.getLoggedInStaff();
    var activity =
        activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);

    if (!activity.getAuthor().equals(staff)) {
      throw new UserNotAuthorizedException();
    }

    var draft =
        ActivityDraft.toDomain(
            activityId,
            activity.getCreatedAt(),
            activity.getUpdatedAt(),
            activity.getTitle(),
            activity.getAuthor(),
            activity.getThematic(),
            activity.getSummary(),
            activity.getDescription(),
            activity.getRecommendedCompletionContexts().orElse(null),
            activity.getStartDate().orElse(null),
            activity.getEndDate().orElse(null),
            activity.getTraceAllowedAssociations(),
            activity.getFeedbackAllowedIterations(),
            activity.isEnableReflection(),
            activity.getBanner().orElse(null),
            activity.getLinks(),
            activity.getFiles());

    var savedDraft = activityDraftRepository.save(draft);

    if (!hasEnrolledStudents(activity)) {
      activity.setStatus(EActivityStatus.UNPUBLISHED);
      activityRepository.save(activity);
      log.info("Unpublished activity {} after creating edition draft", activityId);
    }

    return savedDraft;
  }

  @Override
  public ActivityDraft duplicateActivity(UUID activityId) {
    var staff = loggedInUserService.getLoggedInStaff();
    var activity =
        activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);

    var now = Instant.now();
    var duplicate =
        ActivityDraft.toDomain(
            UUID.randomUUID(),
            now,
            now,
            activity.getTitle(),
            staff,
            activity.getThematic(),
            activity.getSummary(),
            activity.getDescription(),
            activity.getRecommendedCompletionContexts().orElse(null),
            activity.getStartDate().orElse(null),
            activity.getEndDate().orElse(null),
            activity.getTraceAllowedAssociations(),
            activity.getFeedbackAllowedIterations(),
            activity.isEnableReflection(),
            activity
                .getBanner()
                .map(banner -> fileResourceService.copy(banner.getId()))
                .orElse(null),
            activity.getLinks(),
            activity.getFiles().stream()
                .map(file -> fileResourceService.copy(file.getId()))
                .toList());

    var savedDuplicate = activityDraftRepository.save(duplicate);
    log.info("Duplicated activity {} into draft {}", activityId, savedDuplicate.getId());
    return savedDuplicate;
  }

  private boolean hasEnrolledStudents(Activity activity) {
    return declaredActivityService.countEnrolledStudents(activity) > 0;
  }

  @Override
  public Boolean hasEnrolledStudents(ActivityDraft draft) {
    return activityRepository.findById(draft.getId()).map(this::hasEnrolledStudents).orElse(false);
  }

  @Override
  public File uploadDraftBanner(
      UUID activityDraftId, String fileName, String mimeType, long size, byte[] content) {
    var draft = getOwnedDraft(activityDraftId);
    if (!EFileType.fromMimeType(mimeType).isImage()) {
      throw new FileTypeNotSupportedException();
    }
    var file = fileResourceService.upload(fileName, mimeType, size, content, false);
    draft.setBanner(file);
    activityDraftRepository.save(draft);
    return file;
  }

  @Override
  public void deleteDraftBanner(UUID activityDraftId) {
    var draft = getOwnedDraft(activityDraftId);
    draft.getBanner().ifPresent(banner -> fileResourceService.delete(banner.getId()));
    draft.setBanner(null);
    activityDraftRepository.save(draft);
  }

  @Override
  public File addDraftFile(
      UUID activityDraftId, String fileName, String mimeType, long size, byte[] content) {
    var draft = getOwnedDraft(activityDraftId);
    if (!ALLOWED_DRAFT_FILE_TYPES.contains(EFileType.fromMimeType(mimeType))) {
      throw new FileTypeNotSupportedException();
    }
    var file = fileResourceService.upload(fileName, mimeType, size, content, true);
    draft.addFile(file);
    activityDraftRepository.save(draft);
    return file;
  }

  @Override
  public void deleteDraftFile(UUID activityDraftId, UUID fileId) {
    var draft = getOwnedDraft(activityDraftId);
    if (draft.getFiles().stream().noneMatch(file -> file.getId().equals(fileId))) {
      throw new FileNotFoundException();
    }
    fileResourceService.delete(fileId);
    draft.removeFile(fileId);
    activityDraftRepository.save(draft);
  }

  @Override
  public FileDownload downloadActivityFile(UUID activityId, UUID fileId) {
    var loggedInUser = loggedInUserService.getLoggedInUser();
    var activity =
        activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);

    boolean isAuthor = activity.getAuthor().getUser().equals(loggedInUser);
    boolean isEnrolledStudent =
        studentRepository
            .findById(loggedInUser.getId())
            .map(student -> declaredActivityService.isEnrolled(activity, student))
            .orElse(false);

    if (!isAuthor && !isEnrolledStudent) {
      throw new UserNotAuthorizedException();
    }
    if (activity.getFiles().stream().noneMatch(file -> file.getId().equals(fileId))) {
      throw new FileNotFoundException();
    }
    return fileResourceService.download(fileId);
  }

  private ActivityDraft getOwnedDraft(UUID activityDraftId) {
    var staff = loggedInUserService.getLoggedInStaff();
    var draft =
        activityDraftRepository
            .findById(activityDraftId)
            .orElseThrow(ActivityDraftNotFoundException::new);
    if (!draft.getAuthor().equals(staff)) {
      throw new ActivityDraftNotFoundException();
    }
    return draft;
  }
}
