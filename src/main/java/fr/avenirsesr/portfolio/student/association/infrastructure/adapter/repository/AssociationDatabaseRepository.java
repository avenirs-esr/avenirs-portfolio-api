package fr.avenirsesr.portfolio.student.association.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.output.repository.AssociationRepository;
import fr.avenirsesr.portfolio.student.association.infrastructure.adapter.mapper.AssociationMapper;
import fr.avenirsesr.portfolio.student.association.infrastructure.adapter.model.AssociationEntity;
import fr.avenirsesr.portfolio.student.association.infrastructure.adapter.specification.AssociationSpecification;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
  public List<Association> findAllOf(
      List<UUID> ids, Class<?> clazz, List<EAssociationType> associationTypes) {
    if (ids.isEmpty()) {
      return List.of();
    }

    Specification<AssociationEntity> specification = null;

    for (EAssociationType type : associationTypes) {
      Specification<AssociationEntity> typeSpec =
          AssociationSpecification.withTypeIn(List.of(type));

      if (type.getKey1().equals(clazz)) {
        typeSpec = typeSpec.and(AssociationSpecification.key1In(ids));
      } else if (type.getKey2().equals(clazz)) {
        typeSpec = typeSpec.and(AssociationSpecification.key2In(ids));
      } else {
        throw new IllegalArgumentException(
            "Class " + clazz + " not compatible with association type " + type);
      }

      specification = specification == null ? typeSpec : specification.or(typeSpec);
    }

    return jpaRepository.findAll(specification, DEFAULT_SORT).stream()
        .map(AssociationMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public Map<UUID, Long> countAllOf(
      List<UUID> ids, Class<?> clazz, EAssociationType associationType) {
    if (ids.isEmpty()) {
      return Map.of();
    }

    List<AssociationJpaRepository.AssociationCountRow> rows;
    if (associationType.getKey1().equals(clazz)) {
      rows = jpaRepository.countGroupedById1(associationType, ids);
    } else if (associationType.getKey2().equals(clazz)) {
      rows = jpaRepository.countGroupedById2(associationType, ids);
    } else {
      throw new IllegalArgumentException(
          "Class " + clazz + " not compatible with association type " + associationType);
    }

    return rows.stream()
        .collect(
            Collectors.toMap(
                AssociationJpaRepository.AssociationCountRow::getSubjectId,
                AssociationJpaRepository.AssociationCountRow::getTotal));
  }

  @Override
  public List<Association> findAllOf(
      UUID id, Class<?> clazz, List<EAssociationType> associationTypes) {
    Specification<AssociationEntity> specification = null;

    for (EAssociationType type : associationTypes) {
      Specification<AssociationEntity> typeSpec =
          AssociationSpecification.withTypeIn(List.of(type));

      if (type.getKey1().equals(clazz)) {
        typeSpec = typeSpec.and(AssociationSpecification.key1(id));
      } else if (type.getKey2().equals(clazz)) {
        typeSpec = typeSpec.and(AssociationSpecification.key2(id));
      } else {
        throw new IllegalArgumentException(
            "Class " + clazz + " not compatible with association type " + type);
      }

      if (specification == null) {
        specification = typeSpec;
      } else {
        specification = specification.or(typeSpec);
      }
    }

    return jpaRepository.findAll(specification, DEFAULT_SORT).stream()
        .map(AssociationMapper.INSTANCE::toDomain)
        .toList();
  }
}
