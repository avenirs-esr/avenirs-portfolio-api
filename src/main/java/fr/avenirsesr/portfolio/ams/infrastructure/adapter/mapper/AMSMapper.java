package fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSTranslationEntity;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public interface AMSMapper {
  static AMSEntity fromDomain(AMS ams) {
    return AMSEntity.of(
        ams.getId(),
        StudentMapper.fromDomain(ams.getStudent()),
        ams.getStatus(),
        ams.getStartDate(),
        ams.getEndDate());
  }

  static AMS toDomain(AMSEntity entity) {
    AMSTranslationEntity translationEntity =
        TranslationUtil.getTranslation(entity.getTranslations());
    return AMS.toDomain(
        entity.getId(),
        StudentMapper.toDomain(entity.getStudent()),
        translationEntity.getTitle(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
