package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionTranslationEntity;

public interface InstitutionMapper {
  static InstitutionEntity fromDomain(Institution institution) {
    return new InstitutionEntity(institution.getId(), institution.getEnabledFields());
  }

  static Institution toDomain(InstitutionEntity institutionEntity) {
    ELanguage language = ELanguage.FRENCH;
    String name =
        institutionEntity.getTranslations().stream()
            .filter(t -> t.getLanguage().equals(language))
            .findFirst()
            .map(InstitutionTranslationEntity::getName)
            .orElse(null);
    return Institution.toDomain(
        institutionEntity.getId(), name, institutionEntity.getEnabledFields(), language);
  }
}
