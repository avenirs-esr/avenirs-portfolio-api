package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityViewEntity;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ActivityViewJpaRepository extends JpaRepository<ActivityViewEntity, UUID> {
  @Transactional
  @Modifying
  @Query(
      """
      update ActivityViewEntity v
      set v.lastViewedAt = :viewedAt, v.updatedAt = :viewedAt
      where v.activityId = :activityId and v.studentId = :studentId
      """)
  int updateLastViewedAt(
      @Param("activityId") UUID activityId,
      @Param("studentId") UUID studentId,
      @Param("viewedAt") Instant viewedAt);

  int countByActivityId(UUID activityId);

  int countByActivityIdAndStudentIdInAndLastViewedAtGreaterThanEqual(
      UUID activityId, Collection<UUID> studentIds, Instant since);

  List<ActivityViewEntity> findByActivityIdAndStudentIdIn(
      UUID activityId, Collection<UUID> studentIds);

  @Transactional
  void deleteByActivityId(UUID activityId);
}
