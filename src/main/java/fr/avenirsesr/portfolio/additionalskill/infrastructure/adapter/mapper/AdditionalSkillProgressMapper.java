package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;

public interface AdditionalSkillProgressMapper {
  static AdditionalSkillProgressEntity toEntity(AdditionalSkillProgress additionalSkillProgress) {

    return AdditionalSkillProgressEntity.create(
        UserMapper.fromDomain(additionalSkillProgress.getStudent()),
        additionalSkillProgress.getSkill().getId(),
        additionalSkillProgress.getLevel());
  }

  static AdditionalSkillProgress toDomain(
      AdditionalSkillProgressEntity entity, AdditionalSkillCache additionalSkillCache) {

    AdditionalSkill skill = additionalSkillCache.findById(entity.getAdditionalSkillId());

    return AdditionalSkillProgress.toDomain(
        entity.getId(),
        UserMapper.toDomain(entity.getStudent()).toStudent(),
        skill,
        entity.getLevel());
  }
}
