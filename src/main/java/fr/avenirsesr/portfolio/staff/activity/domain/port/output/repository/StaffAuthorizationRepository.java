package fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffAuthorizationRepository extends GenericRepositoryPort<ActivityAuthorization> {
  List<ActivityAuthorization> findAllByActivityId(UUID activityId);

  Optional<ActivityAuthorization> findByActivityIdAndStaffId(UUID activityId, UUID staffId);

  List<ActivityAuthorization> findAllByActivityIds(Collection<UUID> activityIds);

  List<ActivityAuthorization> findAllByActivityIdsAndStaffId(
      Collection<UUID> activityIds, UUID staffId);

  boolean existsByStaffId(UUID staffId);

  void deleteAllByActivityId(UUID activityId);
}
