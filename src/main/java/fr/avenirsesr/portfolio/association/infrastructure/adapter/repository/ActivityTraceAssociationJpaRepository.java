package fr.avenirsesr.portfolio.association.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.ActivityTraceAssociationEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityTraceAssociationJpaRepository
    extends JpaRepository<ActivityTraceAssociationEntity, UUID>,
        JpaSpecificationExecutor<ActivityTraceAssociationEntity> {
  List<ActivityTraceAssociationEntity> findAllByActivityIdAndTraceId(UUID activityId, UUID traceId);
}
