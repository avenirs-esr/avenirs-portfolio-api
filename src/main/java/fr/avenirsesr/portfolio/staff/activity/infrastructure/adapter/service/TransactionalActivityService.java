package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.FileDownload;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityDashboardData;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.staff.activity.domain.data.InactiveStudentData;
import fr.avenirsesr.portfolio.staff.activity.domain.data.SubActivityOverviewData;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAssessment;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityAssessmentType;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityToAssessData;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EActivityAssessmentStatus;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class TransactionalActivityService implements ActivityService {

  private final ActivityService delegate;

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
    return delegate.create(
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
        links);
  }

  @Override
  public Activity createSubActivity(
      UUID id,
      UUID previousActivityId,
      String title,
      String description,
      String recommendedCompletionContexts,
      LocalDate startDate,
      LocalDate endDate,
      List<String> links) {
    return delegate.createSubActivity(
        id,
        previousActivityId,
        title,
        description,
        recommendedCompletionContexts,
        startDate,
        endDate,
        links);
  }

  @Transactional
  @Override
  public Activity publish(UUID activityDraftId) {
    return delegate.publish(activityDraftId);
  }

  @Override
  public Activity unpublish(UUID activityId) {
    return delegate.unpublish(activityId);
  }

  @Override
  public void deleteDraft(UUID activityDraftId) {
    delegate.deleteDraft(activityDraftId);
  }

  @Override
  public void deleteActivityDefinitively(UUID activityId) {
    delegate.deleteActivityDefinitively(activityId);
  }

  @Override
  public Activity getActivityById(UUID id) {
    return delegate.getActivityById(id);
  }

  @Override
  public ActivityDraft getActivityDraftById(UUID id) {
    return delegate.getActivityDraftById(id);
  }

  @Override
  public Activity getMainActivityOf(Activity activity) {
    return delegate.getMainActivityOf(activity);
  }

  @Override
  public List<SubActivityOverviewData> getSubActivityOverviews(UUID activityId) {
    return delegate.getSubActivityOverviews(activityId);
  }

  @Override
  public List<SubActivityOverviewData> getDraftSubActivityOverviews(UUID activityDraftId) {
    return delegate.getDraftSubActivityOverviews(activityDraftId);
  }

  @Override
  public ActivityPresentationData getActivityPresentation(EActivityStatus activityStatus, UUID id) {
    return delegate.getActivityPresentation(activityStatus, id);
  }

  @Override
  public ActivityDashboardData getActivityDashboard(UUID activityId) {
    return delegate.getActivityDashboard(activityId);
  }

  @Override
  public List<InactiveStudentData> getInactiveStudents(UUID activityId) {
    return delegate.getInactiveStudents(activityId);
  }

  @Override
  public Map<EActivityThematic, List<Activity>> getActivityNavigation() {
    return delegate.getActivityNavigation();
  }

  @Override
  public PagedResult<ActivityWithStudentStatusData> activitiesView(
      EActivityThematic thematic, PageCriteria pageCriteria) {
    return delegate.activitiesView(thematic, pageCriteria);
  }

  @Override
  public PagedResult<ActivityWithStudentStatusData> latestActivitiesView(
      PageCriteria pageCriteria) {
    return delegate.latestActivitiesView(pageCriteria);
  }

  @Override
  public PagedResult<ActivityStaffOverviewData> staffActivityWorkingSpace(
      PageCriteria pageCriteria, EActivityStatus activityStatus) {
    return delegate.staffActivityWorkingSpace(pageCriteria, activityStatus);
  }

  @Override
  public PagedResult<ActivityStaffOverviewData> staffActivityLibrary(
      EActivityThematic thematic, PageCriteria pageCriteria) {
    return delegate.staffActivityLibrary(thematic, pageCriteria);
  }

  @Override
  public ActivityDraft createActivityDraft(String title) {
    return delegate.createActivityDraft(title);
  }

  @Override
  public ActivityDraft createSubActivityDraft(UUID activityDraftId, String title) {
    return delegate.createSubActivityDraft(activityDraftId, title);
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
      List<String> links,
      Boolean enableCompletionPeriod,
      List<UUID> targetInstitutionIds,
      List<UUID> targetGroupIds,
      Boolean national,
      ActivityAssessment assessment) {
    return delegate.updateActivityDraft(
        id,
        title,
        thematic,
        summary,
        description,
        recommendedCompletionContexts,
        startDate,
        endDate,
        traceAllowedAssociations,
        feedbackAllowedIterations,
        enableReflection,
        links,
        enableCompletionPeriod,
        targetInstitutionIds,
        targetGroupIds,
        national,
        assessment);
  }

  @Override
  public ActivityDraft createDraftFromActivity(UUID activityId) {
    return delegate.createDraftFromActivity(activityId);
  }

  @Override
  public ActivityDraft duplicateActivity(UUID activityId, String title) {
    return delegate.duplicateActivity(activityId, title);
  }

  @Override
  public Boolean hasEnrolledStudents(ActivityDraft draft) {
    return delegate.hasEnrolledStudents(draft);
  }

  @Override
  public Boolean hasDraftForActivity(UUID activityId) {
    return delegate.hasDraftForActivity(activityId);
  }

  @Override
  public File uploadDraftBanner(
      UUID activityDraftId, String fileName, String mimeType, long size, byte[] content) {
    return delegate.uploadDraftBanner(activityDraftId, fileName, mimeType, size, content);
  }

  @Override
  public void deleteDraftBanner(UUID activityDraftId) {
    delegate.deleteDraftBanner(activityDraftId);
  }

  @Override
  public File addDraftFile(
      UUID activityDraftId, String fileName, String mimeType, long size, byte[] content) {
    return delegate.addDraftFile(activityDraftId, fileName, mimeType, size, content);
  }

  @Override
  public void deleteDraftFile(UUID activityDraftId, UUID fileId) {
    delegate.deleteDraftFile(activityDraftId, fileId);
  }

  @Override
  public FileDownload downloadActivityFile(UUID activityId, UUID fileId) {
    return delegate.downloadActivityFile(activityId, fileId);
  }

  @Override
  public FileDownload downloadDraftFile(UUID activityDraftId, UUID fileId) {
    return delegate.downloadDraftFile(activityDraftId, fileId);
  }

  @Override
  public PagedResult<Activity> getActivitiesWithFeedbacks(
      List<EFeedbackStatus> statuses, PageCriteria pageCriteria) {
    return delegate.getActivitiesWithFeedbacks(statuses, pageCriteria);
  }

  @Override
  public PagedResult<DeclaredActivityToAssessData> getDeclaredActivitiesToAssess(
      List<EActivityAssessmentStatus> statuses,
      List<EActivityAssessmentType> types,
      PageCriteria pageCriteria) {
    return delegate.getDeclaredActivitiesToAssess(statuses, types, pageCriteria);
  }
}
