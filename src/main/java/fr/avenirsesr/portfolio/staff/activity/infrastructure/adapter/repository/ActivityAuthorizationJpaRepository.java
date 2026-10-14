package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityAuthorizationEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

public interface ActivityAuthorizationJpaRepository
    extends JpaRepository<ActivityAuthorizationEntity, UUID>,
        JpaSpecificationExecutor<ActivityAuthorizationEntity> {
  List<ActivityAuthorizationEntity> findAllByActivityId(UUID activityId);

  Optional<ActivityAuthorizationEntity> findByActivityIdAndStaff_Id(UUID activityId, UUID staffId);

  List<ActivityAuthorizationEntity> findAllByActivityIdIn(Collection<UUID> activityIds);

  @Transactional
  void deleteAllByActivityId(UUID activityId);
}
