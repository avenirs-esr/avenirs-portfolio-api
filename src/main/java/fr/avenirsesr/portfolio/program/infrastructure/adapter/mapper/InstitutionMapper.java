package fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.program.domain.model.Institution;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionTranslationEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.TranslationUtil;

public interface InstitutionMapper {
  static InstitutionEntity fromDomain(Institution institution) {
    return InstitutionEntity.of(institution.getId(), institution.getEnabledFields());
  }

  static Institution toDomain(InstitutionEntity entity) {
    InstitutionTranslationEntity translationEntity =
        TranslationUtil.getTranslation(entity.getTranslations());
    return Institution.toDomain(
        entity.getId(), translationEntity.getName(), entity.getEnabledFields());
  }
}
