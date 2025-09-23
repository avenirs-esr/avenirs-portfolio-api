package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;

public interface AdditionalSkillProgressMapper {
  static AdditionalSkillProgressEntity fromDomain(AdditionalSkillProgress additionalSkillProgress) {

    return AdditionalSkillProgressEntity.create(
        additionalSkillProgress.getId(),
        UserMapper.fromDomain(additionalSkillProgress.getStudent()),
        AdditionalSkillMapper.fromDomain(additionalSkillProgress.getSkill()),
        additionalSkillProgress.getLevel());
  }

  static AdditionalSkillProgress toDomain(AdditionalSkillProgressEntity entity) {
    return AdditionalSkillProgress.toDomain(
        entity.getId(),
        UserMapper.toDomain(entity.getStudent()).toStudent(),
        AdditionalSkillMapper.toDomain(entity.getAdditionalSkill()),
        entity.getLevel(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
