package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.ESkillLevel;
import fr.avenirsesr.portfolio.student.skill.domain.port.output.seeder.SkillProgressDataGenerator;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.model.SkillProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class FakeSkillProgress {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeSkillProgress.class, SharedDataGenerator.class);
  private static final DataGeneratorProvider<SkillProgressDataGenerator> skillProgressGenerator =
      new DataGeneratorProvider<SkillProgressDataGenerator>()
          .init(FakeSkillProgress.class, SkillProgressDataGenerator.class);
  private final SkillProgressEntity skillProgressEntity;

  private FakeSkillProgress(SkillProgressEntity skillProgressEntity) {
    this.skillProgressEntity = skillProgressEntity;
  }

  public static FakeSkillProgress of(
      StudentEntity student, List<Skill> availableSkills, List<UUID> bannedSkillsIds) {
    Skill randomSkill = getRandomSkill(availableSkills, bannedSkillsIds);

    return new FakeSkillProgress(
        SkillProgressEntity.of(
            dataGenerator.with("id").uuid(),
            student,
            SkillMapper.INSTANCE.fromDomain(randomSkill),
            dataGenerator.with("ESkillLevel").pickIn(ESkillLevel.class),
            skillProgressGenerator.with("sentence").reflection(),
            false,
            Instant.now(),
            Instant.now()));
  }

  public SkillProgressEntity toEntity() {
    return skillProgressEntity;
  }

  private static Skill getRandomSkill(List<Skill> availableSkills, List<UUID> bannedIds) {
    if (availableSkills.size() <= 2) {
      throw new IllegalStateException("The list must contain more than 2 items.");
    }

    List<Skill> allowedSkills =
        availableSkills.stream()
            .skip(2)
            .filter(skill -> !bannedIds.contains(skill.getId()))
            .toList();

    if (allowedSkills.isEmpty()) {
      throw new IllegalStateException("No IDs available after excluding banned IDs.");
    }

    return allowedSkills.get(dataGenerator.with("skill").number(allowedSkills.size()));
  }
}
