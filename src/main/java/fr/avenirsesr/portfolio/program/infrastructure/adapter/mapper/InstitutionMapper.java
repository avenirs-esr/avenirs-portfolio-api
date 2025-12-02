package fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.program.domain.model.Institution;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionTranslationEntity;

public interface InstitutionMapper {
  static InstitutionEntity fromDomain(Institution institution) {
    return InstitutionEntity.of(institution.getId(), institution.getEnabledFields());
  }

  static Institution toDomain(InstitutionEntity entity) {
    InstitutionTranslationEntity translationEntity =
        TranslationUtil.getTranslation(entity.getTranslations());
    return Institution.toDomain(
        entity.getId(),
        translationEntity.getName(),
        entity.getEnabledFields(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  static Institution toDomain(InstitutionTranslationEntity translationEntity) {
    InstitutionEntity entity = translationEntity.getInstitution();
    return Institution.toDomain(
        entity.getId(),
        translationEntity.getName(),
        entity.getEnabledFields(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
