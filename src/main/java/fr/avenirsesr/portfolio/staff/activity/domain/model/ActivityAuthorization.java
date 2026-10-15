package fr.avenirsesr.portfolio.staff.activity.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ActivityAuthorization extends AvenirsBaseModel {
  private final UUID activityId;
  private final Staff staff;
  private Set<EActivityRight> rights;

  private ActivityAuthorization(
      UUID id,
      UUID activityId,
      Staff staff,
      Set<EActivityRight> rights,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.activityId = activityId;
    this.staff = staff;
    setRights(rights);
  }

  public static ActivityAuthorization create(
      UUID activityId, Staff staff, Set<EActivityRight> rights) {
    var now = Instant.now();
    return new ActivityAuthorization(UUID.randomUUID(), activityId, staff, rights, now, now);
  }

  public static ActivityAuthorization toDomain(
      UUID id,
      UUID activityId,
      Staff staff,
      Set<EActivityRight> rights,
      Instant createdAt,
      Instant updatedAt) {
    return new ActivityAuthorization(id, activityId, staff, rights, createdAt, updatedAt);
  }

  public Set<EActivityRight> getRights() {
    return Collections.unmodifiableSet(rights);
  }

  public void setRights(Set<EActivityRight> rights) {
    this.rights =
        rights == null || rights.isEmpty()
            ? EnumSet.noneOf(EActivityRight.class)
            : EnumSet.copyOf(rights);
  }

  public boolean has(EActivityRight right) {
    return rights.contains(right);
  }

  public boolean hasAnyOf(Collection<EActivityRight> candidates) {
    return candidates.stream().anyMatch(rights::contains);
  }

  public boolean hasAllOf(Collection<EActivityRight> candidates) {
    return rights.containsAll(candidates);
  }

  public boolean isLeader() {
    return has(EActivityRight.LEADER);
  }
}
