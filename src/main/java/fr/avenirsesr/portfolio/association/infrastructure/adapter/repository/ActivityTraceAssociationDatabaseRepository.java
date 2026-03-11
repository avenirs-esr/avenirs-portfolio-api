package fr.avenirsesr.portfolio.association.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.association.domain.data.ActivityTraceAssociationData;
import fr.avenirsesr.portfolio.association.domain.model.ActivityTraceAssociation;
import fr.avenirsesr.portfolio.association.domain.port.output.repository.ActivityTraceAssociationRepository;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.mapper.ActivityTraceAssociationMapper;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.ActivityTraceAssociationEntity;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.specification.ActivityTraceAssociationSpecification;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityTraceAssociationDatabaseRepository
    extends GenericJpaRepositoryAdapter<ActivityTraceAssociation, ActivityTraceAssociationEntity>
    implements ActivityTraceAssociationRepository {
  private final ActivityTraceAssociationJpaRepository jpaRepository;

  protected ActivityTraceAssociationDatabaseRepository(
      ActivityTraceAssociationJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        ActivityTraceAssociationEntity.class,
        ActivityTraceAssociationMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<ActivityTraceAssociation> findAllIn(List<ActivityTraceAssociationData> associations) {
    var sort = Sort.by(Sort.Direction.DESC, "createdAt");
    return jpaRepository
        .findAll(ActivityTraceAssociationSpecification.in(associations), sort)
        .stream()
        .map(ActivityTraceAssociationMapper.INSTANCE::toDomain)
        .toList();
  }
}
