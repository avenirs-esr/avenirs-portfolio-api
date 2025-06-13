package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionTranslationEntity;
import java.util.UUID;

public interface InstitutionTranslationMapper {
  static InstitutionTranslationEntity fromDomain(Institution institution) {
    return new InstitutionTranslationEntity(
        UUID.randomUUID(),
        institution.getLanguage(),
        institution.getName(),
        InstitutionMapper.fromDomain(institution));
  }

  static Institution toDomain(InstitutionTranslationEntity institutionTranslationEntity) {
    return Institution.toDomain(
        institutionTranslationEntity.getInstitution().getId(),
        institutionTranslationEntity.getName(),
        institutionTranslationEntity.getInstitution().getEnabledFields(),
        institutionTranslationEntity.getLanguage());
  }
}
