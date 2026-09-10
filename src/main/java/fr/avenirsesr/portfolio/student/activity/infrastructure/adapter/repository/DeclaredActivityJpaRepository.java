package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeclaredActivityJpaRepository
    extends JpaRepository<DeclaredActivityEntity, UUID>,
        JpaSpecificationExecutor<DeclaredActivityEntity> {
  Optional<DeclaredActivityEntity> findByStudentIdAndActivityId(UUID studentId, UUID activityId);

  int countByActivityIdAndUnsubscribedAtIsNull(UUID activityId);

  int countByActivityIdAndUnsubscribedAtGreaterThanEqual(UUID activityId, Instant since);
}
