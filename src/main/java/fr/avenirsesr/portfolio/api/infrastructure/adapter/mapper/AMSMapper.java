package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSEntity;

public interface AMSMapper {
  static AMSEntity fromDomain(AMS ams) {
    return new AMSEntity(
        ams.getId(),
        UserMapper.fromDomain(ams.getUser()),
        ams.getSkillLevels().stream().map(SkillLevelMapper::fromDomain).toList());
  }

  static AMS toDomain(AMSEntity entity) {
    return AMS.toDomain(
        entity.getId(),
        UserMapper.toDomain(entity.getUser()),
        entity.getSkillLevels().stream().map(SkillLevelMapper::toDomain).toList());
  }
}
