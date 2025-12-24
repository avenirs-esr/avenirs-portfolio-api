package fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.program.domain.model.Institution;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionTranslationEntity;

public class InstitutionMapper implements Mapper<InstitutionEntity, Institution> {
  public static final InstitutionMapper INSTANCE = new InstitutionMapper();

  @Override
  public InstitutionEntity fromDomain(Institution institution) {
    return InstitutionEntity.of(institution.getId());
  }

  @Override
  public Institution toDomain(InstitutionEntity entity) {
    InstitutionTranslationEntity translationEntity =
        TranslationUtil.getTranslation(entity.getTranslations());

    return Institution.toDomain(
        entity.getId(), translationEntity.getName(), entity.getCreatedAt(), entity.getUpdatedAt());
  }
}
