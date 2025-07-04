package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.student.progress.domain.model.Skill;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillTranslationEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import java.util.Set;
import java.util.stream.Collectors;

public interface SkillMapper {
  static SkillEntity fromDomain(Skill skill, TrainingPathEntity trainingPathEntity) {
    var skillEntity = SkillEntity.of(skill.getId(), Set.of(), trainingPathEntity);
    var levels =
        skill.getSkillLevels().stream()
            .map(
                skillLevel ->
                    SkillLevelMapper.fromDomain(
                        skillLevel,
                        skillEntity,
                        skillLevel.getTraces().stream().map(TraceMapper::fromDomain).toList()))
            .collect(Collectors.toSet());
    skillEntity.setSkillLevels(levels);
    return skillEntity;
  }

  static Skill toDomain(SkillEntity skillEntity, TrainingPath trainingPath) {

    SkillTranslationEntity skillTranslationEntity =
        TranslationUtil.getTranslation(skillEntity.getTranslations());
    var skill = Skill.toDomain(skillEntity.getId(), skillTranslationEntity.getName(), Set.of());

    skill.setSkillLevels(
        skillEntity.getSkillLevels().stream()
            .map(skillLevelEntity -> SkillLevelMapper.toDomain(skillLevelEntity, skill))
            .collect(Collectors.toSet()));

    skill.setTrainingPath(trainingPath);

    return skill;
  }

  static Skill toDomainWithoutRecursion(SkillEntity skillEntity, TrainingPath trainingPath) {
    SkillTranslationEntity skillTranslationEntity =
        TranslationUtil.getTranslation(skillEntity.getTranslations());
    var skill = Skill.toDomain(skillEntity.getId(), skillTranslationEntity.getName(), Set.of());
    skill.setTrainingPath(trainingPath);
    return skill;
  }

  static Skill toDomainWithoutRecursion(SkillEntity skillEntity) {
    return toDomainWithoutRecursion(skillEntity, null);
  }
}
