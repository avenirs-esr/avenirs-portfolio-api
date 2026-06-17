package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.FeedbackEntity;
import java.util.List;
import java.util.Set;
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

  @Query(
      value =
          """
          select distinct (trace_snapshot ->> 'attachmentId')::uuid
          from feedback f
          cross join lateral jsonb_array_elements(f.associations::jsonb -> 'traces') trace_snapshot
          where f.declared_activity in (:declaredActivityIds)
            and (trace_snapshot ->> 'id')::uuid in (:traceIds)
            and trace_snapshot ->> 'attachmentId' is not null
          """,
      nativeQuery = true)
  Set<UUID> findAttachmentIdsUsedByTraceSnapshots(
      @Param("declaredActivityIds") List<UUID> declaredActivityIds,
      @Param("traceIds") List<UUID> traceIds);
}
