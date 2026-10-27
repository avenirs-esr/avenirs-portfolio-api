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

  Set<EActivityRight> getRights(Staff staff, UUID activityId);

  Set<EActivityRight> getLoggedInStaffRights(UUID activityId);

  Map<UUID, Set<EActivityRight>> getLoggedInStaffRightsByActivityIds(Collection<UUID> activityIds);

  boolean hasLoggedInStaffAnyRight();

  boolean hasLoggedInStaffAuthorizationOnAnyActivity(EActivityRight right);

  List<ActivityAuthorization> getActivityAuthorizations(UUID activityId);

  List<ActivityAuthorization> defineActivityAuthorizations(
      UUID activityId, List<ActivityAuthorizationDefinition> definitions);

  void grantAllRights(UUID activityId, Staff staff);

  List<Staff> getStaffsWithRight(UUID activityId, EActivityRight right);

  Map<UUID, List<Staff>> getLeadersByActivityIds(Collection<UUID> activityIds);

  void deleteAllOfActivity(UUID activityId);

  boolean hasDraftRight(Staff staff, UUID activityDraftId, EActivityRight right);

  boolean hasAnyDraftRight(Staff staff, UUID activityDraftId, Set<EActivityRight> rights);

  void checkDraftRight(Staff staff, UUID activityDraftId, EActivityRight right);

  void checkAnyDraftRight(Staff staff, UUID activityDraftId, Set<EActivityRight> rights);

  Set<EActivityRight> getLoggedInStaffDraftRights(UUID activityDraftId);

  Map<UUID, Set<EActivityRight>> getLoggedInStaffDraftRightsByDraftIds(
      Collection<UUID> activityDraftIds);

  boolean hasLoggedInStaffAnyDraftRight();

  List<ActivityAuthorization> getDraftAuthorizations(UUID activityDraftId);

  List<ActivityAuthorization> defineDraftAuthorizations(
      UUID activityDraftId, List<ActivityAuthorizationDefinition> definitions);

  void grantAllDraftRights(UUID activityDraftId, Staff staff);

  Map<UUID, List<Staff>> getLeadersByDraftIds(Collection<UUID> activityDraftIds);

  void deleteAllOfDraft(UUID activityDraftId);

  void copyActivityAuthorizationsToDraft(UUID activityId);

  void applyDraftAuthorizationsToActivity(UUID activityId);
}
