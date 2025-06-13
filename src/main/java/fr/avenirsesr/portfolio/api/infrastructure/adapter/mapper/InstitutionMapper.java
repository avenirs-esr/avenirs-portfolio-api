package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionTranslationEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.util.TranslationUtil;

public interface InstitutionMapper {
  static InstitutionEntity fromDomain(Institution institution) {
    return new InstitutionEntity(institution.getId(), institution.getEnabledFields());
  }

  static Institution toDomain(InstitutionEntity entity) {
    return toDomain(entity, ELanguage.FRENCH);
  }

  static Institution toDomain(InstitutionEntity institutionEntity, ELanguage language) {
    ELanguage fallbackLanguage = ELanguage.FRENCH;
    InstitutionTranslationEntity translationEntity =
        TranslationUtil.getTranslation(
            institutionEntity.getTranslations(), language, fallbackLanguage);
    return Institution.toDomain(
        institutionEntity.getId(),
        translationEntity.getName(),
        institutionEntity.getEnabledFields(),
        language);
  }
}
