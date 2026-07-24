package fr.avenirsesr.portfolio.association.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.AssociationEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssociationJpaRepository
    extends JpaRepository<AssociationEntity, UUID>, JpaSpecificationExecutor<AssociationEntity> {

  @Query(
      """
      select a.id1 as subjectId, count(a) as total
      from AssociationEntity a
      where a.associationType = :associationType and a.id1 in :ids
      group by a.id1
      """)
  List<AssociationCountRow> countGroupedById1(
      @Param("associationType") EAssociationType associationType, @Param("ids") List<UUID> ids);

  @Query(
      """
      select a.id2 as subjectId, count(a) as total
      from AssociationEntity a
      where a.associationType = :associationType and a.id2 in :ids
      group by a.id2
      """)
  List<AssociationCountRow> countGroupedById2(
      @Param("associationType") EAssociationType associationType, @Param("ids") List<UUID> ids);

  interface AssociationCountRow {
    UUID getSubjectId();

    Long getTotal();
  }
}
