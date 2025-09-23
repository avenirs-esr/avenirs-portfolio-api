package fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSTranslationEntity;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;

public interface AMSMapper {
  static AMSEntity fromDomain(AMS ams) {
    return AMSEntity.of(
        ams.getId(),
        UserMapper.fromDomain(ams.getUser()),
        ams.getStatus(),
        ams.getStartDate(),
        ams.getEndDate());
  }

  static AMS toDomain(AMSEntity entity) {
    AMSTranslationEntity translationEntity =
        TranslationUtil.getTranslation(entity.getTranslations());
    return AMS.toDomain(
        entity.getId(),
        UserMapper.toDomain(entity.getUser()),
        translationEntity.getTitle(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
