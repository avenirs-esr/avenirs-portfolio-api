package fr.avenirsesr.portfolio.staff.activity.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.FileDownload;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ActivityService {
  Activity create(
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
      List<String> links);

  Activity publish(UUID activityDraftId);

  Activity unpublish(UUID activityId);

  void deleteDraft(UUID activityDraftId);

  Activity getActivityById(UUID id);

  ActivityDraft getActivityDraftById(UUID id);

  ActivityPresentationData getActivityPresentation(EActivityStatus activityStatus, UUID id);

  Map<EActivityThematic, List<Activity>> getActivityNavigation();

  PagedResult<ActivityWithStudentStatusData> activitiesView(
      EActivityThematic thematic, PageCriteria pageCriteria);

  PagedResult<ActivityWithStudentStatusData> latestActivitiesView(PageCriteria pageCriteria);

  PagedResult<ActivityStaffOverviewData> staffActivityWorkingSpace(
      PageCriteria pageCriteria, EActivityStatus activityStatus);

  PagedResult<ActivityStaffOverviewData> staffActivityLibrary(
      EActivityThematic thematic, PageCriteria pageCriteria);

  ActivityDraft createActivityDraft(String title);

  ActivityDraft updateActivityDraft(
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
      List<String> links);

  ActivityDraft createDraftFromActivity(UUID activityId);

  Boolean hasEnrolledStudents(ActivityDraft draft);

  File uploadDraftBanner(
      UUID activityDraftId, String fileName, String mimeType, long size, byte[] content);

  void deleteDraftBanner(UUID activityDraftId);

  File addDraftFile(
      UUID activityDraftId, String fileName, String mimeType, long size, byte[] content);

  void deleteDraftFile(UUID activityDraftId, UUID fileId);

  FileDownload downloadActivityFile(UUID activityId, UUID fileId);
}
