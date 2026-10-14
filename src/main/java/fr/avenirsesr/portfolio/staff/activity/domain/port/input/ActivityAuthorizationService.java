package fr.avenirsesr.portfolio.staff.activity.domain.port.input;

import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityAuthorizationDefinition;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ActivityAuthorizationService {
  boolean hasRight(Staff staff, UUID activityId, EActivityRight right);

  boolean hasAnyRight(Staff staff, UUID activityId, Set<EActivityRight> rights);

  boolean hasAnyRight(Staff staff, UUID activityId);

  void checkRight(Staff staff, UUID activityId, EActivityRight right);

  void checkAnyRight(Staff staff, UUID activityId, Set<EActivityRight> rights);

  void checkAnyRight(Staff staff, UUID activityId);

  Set<EActivityRight> getRights(Staff staff, UUID activityId);

  Set<EActivityRight> getLoggedInStaffRights(UUID activityId);

  List<ActivityAuthorization> getActivityAuthorizations(UUID activityId);

  List<ActivityAuthorization> defineActivityAuthorizations(
      UUID activityId, List<ActivityAuthorizationDefinition> definitions);

  void grantAllRights(UUID activityId, Staff staff);

  List<Staff> getStaffsWithRight(UUID activityId, EActivityRight right);

  Map<UUID, List<Staff>> getResponsiblesByActivityIds(Collection<UUID> activityIds);

  void deleteAllOfActivity(UUID activityId);
}
