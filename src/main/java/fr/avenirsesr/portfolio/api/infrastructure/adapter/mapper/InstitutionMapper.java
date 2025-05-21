package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionEntity;

public interface InstitutionMapper {
  static InstitutionEntity fromModelToEntity(Institution institution) {
    return new InstitutionEntity();
  }

  static Institution fromEntityToModel(InstitutionEntity institutionEntity) {
    return Institution.toDomain(
        institutionEntity.getId(),
        institutionEntity.getName(),
        institutionEntity.getEnabledFields());
  }
}
