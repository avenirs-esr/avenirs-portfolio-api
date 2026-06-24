package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FeedbackRepository extends GenericRepositoryPort<Feedback> {
  List<Feedback> findAllByDeclaredActivityId(UUID declaredActivityId);

  PagedResult<Feedback> findByStaff(
      UUID staffId, EFeedbackStatus statusFilter, UUID activityId, PageCriteria pageCriteria);

  List<Feedback> findLatestFeedbacksByStaffAndActivityForEachStudent(UUID staffId, UUID activityId);

  List<UUID> findDeclaredActivityIdsHavingActiveFeedbacks(List<UUID> declaredActivityIds);

  Set<UUID> findAttachmentIdsUsedByTraceSnapshots(
      List<UUID> declaredActivityIds, List<UUID> traceIds);

  int countByStatus(Staff staff, Activity activity, EFeedbackStatus status);
}
