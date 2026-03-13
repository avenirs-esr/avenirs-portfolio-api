package fr.avenirsesr.portfolio.association.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.output.repository.AssociationRepository;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.mapper.AssociationMapper;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.AssociationEntity;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.specification.AssociationSpecification;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class AssociationDatabaseRepository
    extends GenericJpaRepositoryAdapter<Association, AssociationEntity>
    implements AssociationRepository {
  private final AssociationJpaRepository jpaRepository;

  private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

  protected AssociationDatabaseRepository(AssociationJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, AssociationEntity.class, AssociationMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<Association> findAllIn(List<AssociationData> associations) {
    return jpaRepository.findAll(AssociationSpecification.in(associations), DEFAULT_SORT).stream()
        .map(AssociationMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public List<Association> findAllOf(List<EAssociationType> associationTypes) {
    return jpaRepository
        .findAll(AssociationSpecification.withTypeIn(associationTypes), DEFAULT_SORT)
        .stream()
        .map(AssociationMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public List<Association> findAllOf(UUID id, Class<?> clazz, EAssociationType associationType) {
    var specification = AssociationSpecification.withTypeIn(List.of(associationType));
    if (associationType.getKey1().equals(clazz))
      specification = specification.and(AssociationSpecification.key1(id));
    else if (associationType.getKey2().equals(clazz))
      specification = specification.and(AssociationSpecification.key2(id));
    else throw new IllegalArgumentException();

    return jpaRepository.findAll(specification, DEFAULT_SORT).stream()
        .map(AssociationMapper.INSTANCE::toDomain)
        .toList();
  }
}
