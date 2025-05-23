package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionEntity;

public interface InstitutionMapper {
  static InstitutionEntity fromDomain(Institution institution) {
    return new InstitutionEntity(
        institution.getId(), institution.getName(), institution.getEnabledFields());
  }

  static Institution toDomain(InstitutionEntity institutionEntity) {
    return Institution.toDomain(
        institutionEntity.getId(),
        institutionEntity.getName(),
        institutionEntity.getEnabledFields());
  }
}
