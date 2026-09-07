package fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository;

import java.util.UUID;

/** Lightweight tracking of the activity consultations used to compute the activity key figures. */
public interface ActivityViewRepository {
  /** Records a student consultation, keeping at most one record per student and activity. */
  void recordView(UUID activityId, UUID studentId);

  int countUniqueViews(UUID activityId);
}
