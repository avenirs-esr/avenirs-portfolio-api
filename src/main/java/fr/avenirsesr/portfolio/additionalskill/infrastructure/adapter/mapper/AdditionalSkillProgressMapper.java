package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;

public interface AdditionalSkillProgressMapper {
  static AdditionalSkillProgressEntity fromDomain(AdditionalSkillProgress additionalSkillProgress) {

    return AdditionalSkillProgressEntity.create(
        UserMapper.fromDomain(additionalSkillProgress.getStudent()),
        additionalSkillProgress.getSkill().getId(),
        additionalSkillProgress.getLevel());
  }

  static AdditionalSkillProgress toDomain(AdditionalSkillProgressEntity entity) {
    return AdditionalSkillProgress.toDomain(
        entity.getId(),
        UserMapper.toDomain(entity.getStudent()).toStudent(),
        null,
        entity.getLevel());
  }

  static AdditionalSkillProgress toDomain(
      AdditionalSkillProgressEntity entity, AdditionalSkill skill) {

    return AdditionalSkillProgress.toDomain(
        entity.getId(),
        UserMapper.toDomain(entity.getStudent()).toStudent(),
        skill,
        entity.getLevel());
  }
}
