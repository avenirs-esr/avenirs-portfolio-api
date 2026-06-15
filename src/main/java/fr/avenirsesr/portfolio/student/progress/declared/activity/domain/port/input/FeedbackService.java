package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import java.util.UUID;

public interface FeedbackService {
  Feedback createFeedback(UUID declaredActivityId);

  Feedback getFeedbackDetails(UUID feedbackId);

  void updateFeedback(UUID feedbackId, String feedback);

  PagedResult<Feedback> getStaffFeedbacks(
      EFeedbackStatus statusFilter, UUID activityId, PageCriteria pageCriteria);
}
