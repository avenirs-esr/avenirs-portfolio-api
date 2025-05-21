package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillEntity;
import java.util.stream.Collectors;

public interface SkillMapper {
  static SkillEntity fromDomain(Skill skill) {
    return new SkillEntity(
        skill.getId(),
        skill.getName(),
        skill.getSkillLevels().stream()
            .map(SkillLevelMapper::fromDomain)
            .collect(Collectors.toSet()));
  }

  static Skill toDomain(SkillEntity skillEntity) {
    return Skill.toDomain(
        skillEntity.getId(),
        skillEntity.getName(),
        skillEntity.getSkillLevels().stream()
            .map(SkillLevelMapper::toDomain)
            .collect(Collectors.toSet()));
  }
}
