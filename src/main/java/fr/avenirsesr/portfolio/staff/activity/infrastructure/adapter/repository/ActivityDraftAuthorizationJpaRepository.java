package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityDraftAuthorizationEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

public interface ActivityDraftAuthorizationJpaRepository
    extends JpaRepository<ActivityDraftAuthorizationEntity, UUID>,
        JpaSpecificationExecutor<ActivityDraftAuthorizationEntity> {
  List<ActivityDraftAuthorizationEntity> findAllByActivityDraftId(UUID activityDraftId);

  Optional<ActivityDraftAuthorizationEntity> findByActivityDraftIdAndStaff_Id(
      UUID activityDraftId, UUID staffId);

  List<ActivityDraftAuthorizationEntity> findAllByActivityDraftIdIn(
      Collection<UUID> activityDraftIds);

  @Transactional
  void deleteAllByActivityDraftId(UUID activityDraftId);
}
