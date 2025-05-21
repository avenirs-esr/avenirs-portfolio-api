package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSEntity;

public interface AMSMapper {
  static AMSEntity fromModelToEntity(AMS ams) {
    return new AMSEntity();
  }

  static AMS fromEntityToModel(AMSEntity amsEntity) {
    return AMS.toDomain(
        amsEntity.getId(),
        UserMapper.fromEntityToModel(amsEntity.getUser()),
        amsEntity.getSkillLevels().stream().map(SkillLevelMapper::fromEntityToModel).toList());
  }
}
