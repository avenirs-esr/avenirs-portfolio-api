package fr.avenirsesr.portfolio.interoperability.additionalskill.rome.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.infrastructure.adapter.model.Rome4VersionEntity;

public interface Rome4VersionMapper {

  static Rome4VersionEntity fromDomain(Rome4Version rome4Version) {
    return Rome4VersionEntity.of(
        rome4Version.getId(), rome4Version.getVersion(), rome4Version.getLastModifiedDate());
  }

  static Rome4Version toDomain(Rome4VersionEntity entity) {
    return Rome4Version.toDomain(entity.getId(), entity.getVersion(), entity.getLastModifiedDate());
  }
}
