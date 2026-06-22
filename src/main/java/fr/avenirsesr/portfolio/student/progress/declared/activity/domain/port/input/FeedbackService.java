package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.FeedbackData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FeedbackService {
  Feedback createFeedback(UUID declaredActivityId);

  FeedbackData getFeedbackDetails(UUID feedbackId, EUserCategory userCategory);

  FeedbackData getStudentFeedbackDetails(User loggedInUser, Feedback feedback);

  void updateFeedback(UUID feedbackId, String feedback);

  PagedResult<Feedback> getStaffFeedbacks(
      EFeedbackStatus statusFilter, UUID activityId, PageCriteria pageCriteria);

  List<Feedback> getFeedbacksByActivity(UUID activityId);

  void submitFeedback(UUID feedbackId);

  Set<UUID> findAttachmentIdsUsedByTraceSnapshots(
      List<UUID> declaredActivityIds, List<UUID> traceIds);
}
