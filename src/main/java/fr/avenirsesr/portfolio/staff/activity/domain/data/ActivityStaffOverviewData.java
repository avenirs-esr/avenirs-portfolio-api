package fr.avenirsesr.portfolio.staff.activity.domain.data;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ActivityStaffOverviewData(
    UUID activityId,
    String title,
    EActivityThematic thematic,
    List<Staff> leaders,
    EActivityStatus activityStatus,
    Instant updatedAt) {

  public ActivityStaffOverviewData withLeaders(List<Staff> leaders) {
    return new ActivityStaffOverviewData(
        activityId, title, thematic, leaders, activityStatus, updatedAt);
  }
}
