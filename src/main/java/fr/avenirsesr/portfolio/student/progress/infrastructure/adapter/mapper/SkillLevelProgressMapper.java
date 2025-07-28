package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import java.util.List;

public interface SkillLevelProgressMapper {
  static SkillLevelProgressEntity fromDomain(SkillLevelProgress skillLevelProgress) {
    return SkillLevelProgressEntity.of(
        skillLevelProgress.getId(),
        UserMapper.fromDomain(skillLevelProgress.getStudent()),
        SkillLevelMapper.fromDomain(skillLevelProgress.getSkillLevel()),
        skillLevelProgress.getStatus(),
        skillLevelProgress.getStartDate(),
        skillLevelProgress.getEndDate(),
        skillLevelProgress.getTraces().stream().map(TraceMapper::fromDomain).toList(),
        skillLevelProgress.getAmses().stream().map(AMSMapper::fromDomain).toList());
  }

  static SkillLevelProgress toDomain(SkillLevelProgressEntity entity) {
    return SkillLevelProgress.toDomain(
        entity.getId(),
        UserMapper.toDomain(entity.getStudent()).toStudent(),
        SkillLevelMapper.toDomain(entity.getSkillLevel()),
        entity.getStatus(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getTraces().stream().map(TraceMapper::toDomain).toList(),
        entity.getAmses().stream().map(AMSMapper::toDomain).toList());
  }

  static SkillLevelProgress toDomain(SkillLevelProgressEntity entity, List<AMS> ameses) {
    return SkillLevelProgress.toDomain(
        entity.getId(),
        UserMapper.toDomain(entity.getStudent()).toStudent(),
        SkillLevelMapper.toDomain(entity.getSkillLevel()),
        entity.getStatus(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getTraces().stream().map(TraceMapper::toDomain).toList(),
        ameses);
  }

  static SkillLevelProgress toDomainWithoutRecursion(
      SkillLevelProgressEntity entity, List<AMS> ameses) {
    return SkillLevelProgress.toDomain(
        entity.getId(),
        UserMapper.toDomain(entity.getStudent()).toStudent(),
        SkillLevelMapper.toDomain(entity.getSkillLevel()),
        entity.getStatus(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getTraces().stream().map(TraceMapper::toDomainWithoutRecursion).toList(),
        ameses);
  }
}
