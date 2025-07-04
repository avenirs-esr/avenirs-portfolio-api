package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.domain.model.Institution;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.InstitutionTranslationEntity;
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
