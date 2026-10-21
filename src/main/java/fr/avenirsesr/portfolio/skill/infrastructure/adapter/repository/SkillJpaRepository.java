package fr.avenirsesr.portfolio.skill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.skill.infrastructure.adapter.model.SkillEntity;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SkillJpaRepository
    extends JpaRepository<SkillEntity, UUID>, JpaSpecificationExecutor<SkillEntity> {

  @Query(
      """
      select s.id as id, p.id as parentId
      from SkillEntity s
      left join s.parent p
      where s.id in :ids
      """)
  List<SkillParentProjection> findParentIdsByIdIn(@Param("ids") Collection<UUID> ids);

  @Query(
      """
      select s
      from SkillEntity s
      join fetch s.referential
      where s.id in :ids
      """)
  List<SkillEntity> findAllByIdInFetchingReferential(@Param("ids") Collection<UUID> ids);

  @Query(
      """
      select s
      from SkillEntity s
      join fetch s.referential r
      where r.id = :referentialId
      """)
  List<SkillEntity> findAllByReferentialId(@Param("referentialId") UUID referentialId);

  @Transactional
  @Modifying
  @Query(
      """
      update SkillEntity s
      set s.archived = true, s.updatedAt = :now
      where s.id in :ids and s.archived = false
      """)
  int archiveAllByIdIn(@Param("ids") Collection<UUID> ids, @Param("now") Instant now);
}
