package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.FeedbackEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackJpaRepository
    extends JpaRepository<FeedbackEntity, UUID>, JpaSpecificationExecutor<FeedbackEntity> {
  List<FeedbackEntity> findAllByDeclaredActivity_IdOrderByCreatedAtDesc(UUID declaredActivityId);

  @Query(
      """
      select distinct f.declaredActivity.id
      from FeedbackEntity f
      where f.declaredActivity.id in :declaredActivityIds
      and f.status != 'SUBMITTED'
      """)
  List<UUID> findDeclaredActivityIdsWithActiveFeedbacks(
      @Param("declaredActivityIds") List<UUID> declaredActivityIds);
}
