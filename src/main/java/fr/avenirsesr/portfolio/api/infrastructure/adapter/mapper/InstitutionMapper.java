package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionTranslationEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.util.TranslationUtil;

public interface InstitutionMapper {
  static InstitutionEntity fromDomain(Institution institution) {
    return new InstitutionEntity(institution.getId(), institution.getEnabledFields());
  }

  static Institution toDomain(InstitutionEntity entity) {
    InstitutionTranslationEntity translationEntity =
        TranslationUtil.getTranslation(entity.getTranslations());
    return Institution.toDomain(
        entity.getId(), translationEntity.getName(), entity.getEnabledFields());
  }
}
