package fr.avenirsesr.portfolio.skill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.skill.infrastructure.adapter.model.NationalSkillDetailsEntity;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NationalSkillDetailsJpaRepository
    extends JpaRepository<NationalSkillDetailsEntity, UUID>,
        JpaSpecificationExecutor<NationalSkillDetailsEntity> {

  @Query(
      """
      select d.skill.id as skillId, d.kind as kind
      from NationalSkillDetailsEntity d
      where d.skill.id in :skillIds
      """)
  List<SkillKindProjection> findKindsBySkillIdIn(@Param("skillIds") Collection<UUID> skillIds);

  @Transactional
  @Modifying
  @Query("delete from NationalSkillDetailsEntity d where d.skill.id in :skillIds")
  int deleteBySkillIdIn(@Param("skillIds") Collection<UUID> skillIds);
}
