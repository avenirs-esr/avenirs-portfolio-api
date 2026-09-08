package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeclaredActivityJpaRepository
    extends JpaRepository<DeclaredActivityEntity, UUID>,
        JpaSpecificationExecutor<DeclaredActivityEntity> {
  Optional<DeclaredActivityEntity> findByStudentIdAndActivityId(UUID studentId, UUID activityId);
  Optional<DeclaredActivityEntity> findByIdAndStudentId(UUID id, UUID studentId);
  @Modifying
  @Query("delete from DeclaredActivityEntity d where d.id = :id and d.student.id = :studentId")
  int deleteByIdAndStudentId(@Param("id") UUID id, @Param("studentId") UUID studentId);
  int countByActivityIdAndUnsubscribedAtIsNull(UUID activityId);

  int countByActivityIdAndUnsubscribedAtGreaterThanEqual(UUID activityId, Instant since);
}
