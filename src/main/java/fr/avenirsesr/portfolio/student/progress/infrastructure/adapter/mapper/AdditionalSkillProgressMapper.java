package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.student.progress.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public interface AdditionalSkillProgressMapper {
  static AdditionalSkillProgressEntity fromDomain(AdditionalSkillProgress additionalSkillProgress) {

    return AdditionalSkillProgressEntity.create(
        additionalSkillProgress.getId(),
        StudentMapper.fromDomain(additionalSkillProgress.getStudent()),
        AdditionalSkillMapper.fromDomain(additionalSkillProgress.getSkill()),
        additionalSkillProgress.getLevel());
  }

  static AdditionalSkillProgress toDomain(AdditionalSkillProgressEntity entity) {
    return AdditionalSkillProgress.toDomain(
        entity.getId(),
        StudentMapper.toDomain(entity.getStudent()),
        AdditionalSkillMapper.toDomain(entity.getAdditionalSkill()),
        entity.getLevel(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
