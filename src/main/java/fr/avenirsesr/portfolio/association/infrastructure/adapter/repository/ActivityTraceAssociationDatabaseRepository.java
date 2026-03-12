package fr.avenirsesr.portfolio.association.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.association.domain.data.ActivityTraceAssociationData;
import fr.avenirsesr.portfolio.association.domain.model.ActivityTraceAssociation;
import fr.avenirsesr.portfolio.association.domain.port.output.repository.ActivityTraceAssociationRepository;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.mapper.ActivityTraceAssociationMapper;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.ActivityTraceAssociationEntity;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.specification.ActivityTraceAssociationSpecification;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityTraceAssociationDatabaseRepository
    extends GenericJpaRepositoryAdapter<ActivityTraceAssociation, ActivityTraceAssociationEntity>
    implements ActivityTraceAssociationRepository {
  private final ActivityTraceAssociationJpaRepository jpaRepository;

  private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

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
    return jpaRepository
        .findAll(ActivityTraceAssociationSpecification.in(associations), DEFAULT_SORT)
        .stream()
        .map(ActivityTraceAssociationMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public List<ActivityTraceAssociation> findAllOf(Trace trace) {
    return jpaRepository.findAllByTraceId(trace.getId()).stream()
        .map(ActivityTraceAssociationMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public List<ActivityTraceAssociation> findAllOf(DeclaredActivity declaredActivity) {
    return jpaRepository.findAllByActivityId(declaredActivity.getId()).stream()
        .map(ActivityTraceAssociationMapper.INSTANCE::toDomain)
        .toList();
  }
}
