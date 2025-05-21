package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillEntity;
import java.util.stream.Collectors;

public interface SkillMapper {
  static SkillEntity fromModelToEntity(Skill skill) {
    return new SkillEntity();
  }

  static Skill fromEntityToModel(SkillEntity skillEntity) {
    return Skill.toDomain(
        skillEntity.getId(),
        skillEntity.getName(),
        skillEntity.getSkillLevels().stream()
            .map(SkillLevelMapper::fromEntityToModel)
            .collect(Collectors.toSet()));
  }
}
