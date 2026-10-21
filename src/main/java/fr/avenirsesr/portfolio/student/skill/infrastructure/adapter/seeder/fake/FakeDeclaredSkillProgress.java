package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.student.skill.domain.port.output.seeder.DeclaredSkillProgressDataGenerator;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.model.DeclaredSkillProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class FakeDeclaredSkillProgress {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeDeclaredSkillProgress.class, SharedDataGenerator.class);
  private static final DataGeneratorProvider<DeclaredSkillProgressDataGenerator>
      declaredSkillProgressGenerator =
          new DataGeneratorProvider<DeclaredSkillProgressDataGenerator>()
              .init(FakeDeclaredSkillProgress.class, DeclaredSkillProgressDataGenerator.class);
  private final DeclaredSkillProgressEntity declaredSkillProgressEntity;

  private FakeDeclaredSkillProgress(DeclaredSkillProgressEntity declaredSkillProgressEntity) {
    this.declaredSkillProgressEntity = declaredSkillProgressEntity;
  }

  public static FakeDeclaredSkillProgress of(
      StudentEntity student, List<Skill> availableSkills, List<UUID> bannedSkillsIds) {
    Skill randomSkill = getRandomSkill(availableSkills, bannedSkillsIds);

    return new FakeDeclaredSkillProgress(
        DeclaredSkillProgressEntity.of(
            dataGenerator.with("id").uuid(),
            student,
            SkillMapper.INSTANCE.fromDomain(randomSkill),
            dataGenerator.with("EDeclaredSkillLevel").pickIn(EDeclaredSkillLevel.class),
            declaredSkillProgressGenerator.with("sentence").reflection(),
            false,
            Instant.now(),
            Instant.now()));
  }

  public DeclaredSkillProgressEntity toEntity() {
    return declaredSkillProgressEntity;
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
