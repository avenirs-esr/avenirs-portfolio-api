package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityDraftEntity;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityDraftJpaRepository
    extends JpaRepository<ActivityDraftEntity, UUID>,
        JpaSpecificationExecutor<ActivityDraftEntity> {
  List<ActivityDraftEntity> findAllByMainActivityId(UUID mainActivityId);

  List<ActivityDraftEntity> findAllByIdInAndMainActivityIdIsNotNull(Collection<UUID> ids);

  boolean existsByIdAndMainActivityIdIsNotNull(UUID id);
}
