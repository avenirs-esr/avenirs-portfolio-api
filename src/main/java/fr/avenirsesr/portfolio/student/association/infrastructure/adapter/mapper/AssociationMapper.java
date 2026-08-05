package fr.avenirsesr.portfolio.student.association.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.infrastructure.adapter.model.AssociationEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;

public class AssociationMapper implements Mapper<AssociationEntity, Association> {
  public static AssociationMapper INSTANCE = new AssociationMapper();

  @Override
  public AssociationEntity fromDomain(Association domain) {
    return AssociationEntity.of(
        domain.getId(),
        domain.getId1(),
        domain.getId2(),
        domain.getAssociationType(),
        domain.getCreatedAt(),
        domain.getUpdatedAt());
  }

  @Override
  public Association toDomain(AssociationEntity entity) {
    return Association.toDomain(
        entity.getId(),
        entity.getId1(),
        entity.getId2(),
        entity.getAssociationType(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
