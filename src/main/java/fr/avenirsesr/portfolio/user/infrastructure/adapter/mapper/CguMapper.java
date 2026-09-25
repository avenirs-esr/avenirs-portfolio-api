package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.user.domain.model.Cgu;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.CguEntity;

public class CguMapper implements Mapper<CguEntity, Cgu> {

  public static final CguMapper INSTANCE = new CguMapper();

  @Override
  public CguEntity fromDomain(Cgu domain) {
    return CguEntity.of(
        domain.getId(),
        domain.getCreatedAt(),
        domain.getUpdatedAt(),
        UserMapper.INSTANCE.fromDomain(domain.getUser()),
        domain.getVersionId(),
        domain.getAcceptedAt());
  }

  @Override
  public Cgu toDomain(CguEntity entity) {
    return Cgu.toDomain(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        UserMapper.INSTANCE.toDomain(entity.getUser()),
        entity.getVersionId(),
        entity.getAcceptedAt());
  }
}
