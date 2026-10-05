package fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/** Lightweight tracking of the activity consultations used to compute the activity key figures. */
public interface ActivityViewRepository {
  /**
   * Records a student consultation, keeping at most one record per student and activity and
   * refreshing the date of its last consultation.
   */
  void recordView(UUID activityId, UUID studentId);

  int countUniqueViews(UUID activityId);

  int countViewersSince(UUID activityId, Collection<UUID> studentIds, Instant since);

  /** Date of the last consultation of each given student having consulted the activity. */
  Map<UUID, Instant> findLastViewedAtByStudents(UUID activityId, Collection<UUID> studentIds);

  void deleteAllByActivityId(UUID activityId);
}
