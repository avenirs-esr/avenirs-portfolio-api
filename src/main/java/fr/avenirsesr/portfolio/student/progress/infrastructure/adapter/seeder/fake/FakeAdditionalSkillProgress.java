package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.seeder.AdditionalSkillProgressDataGenerator;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.util.List;
import java.util.UUID;

public class FakeAdditionalSkillProgress {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeAdditionalSkillProgress.class, SharedDataGenerator.class);
  private static final DataGeneratorProvider<AdditionalSkillProgressDataGenerator>
      additionalSkillProgressGenerator =
          new DataGeneratorProvider<AdditionalSkillProgressDataGenerator>()
              .init(FakeAdditionalSkillProgress.class, AdditionalSkillProgressDataGenerator.class);
  private final AdditionalSkillProgressEntity additionalSkillProgressEntity;

  private FakeAdditionalSkillProgress(AdditionalSkillProgressEntity additionalSkillProgressEntity) {
    this.additionalSkillProgressEntity = additionalSkillProgressEntity;
  }

  public static FakeAdditionalSkillProgress of(
      StudentEntity student,
      List<AdditionalSkillEntity> savedAdditionalSkills,
      List<UUID> bannedSkillsIds) {
    AdditionalSkillEntity randomSkill =
        getRandomAdditionalSkill(savedAdditionalSkills, bannedSkillsIds);

    return new FakeAdditionalSkillProgress(
        AdditionalSkillProgressEntity.of(
            dataGenerator.with("id").uuid(),
            student,
            randomSkill,
            dataGenerator.with("EAdditionalSkillLevel").pickIn(EAdditionalSkillLevel.class),
            additionalSkillProgressGenerator.with("sentence").description()));
  }

  public AdditionalSkillProgressEntity toEntity() {
    return additionalSkillProgressEntity;
  }

  private static AdditionalSkillEntity getRandomAdditionalSkill(
      List<AdditionalSkillEntity> savedAdditionalSkills, List<UUID> bannedIds) {

    if (savedAdditionalSkills.size() <= 2) {
      throw new IllegalStateException("The list must contain more than 2 items.");
    }

    List<AdditionalSkillEntity> allowedSkills =
        savedAdditionalSkills.stream()
            .skip(2)
            .filter(skill -> !bannedIds.contains(skill.getId()))
            .toList();

    if (allowedSkills.isEmpty()) {
      throw new IllegalStateException("No IDs available after excluding banned IDs.");
    }

    return allowedSkills.get(
        dataGenerator.with("CompetenceComplementaireDetaillee").number(allowedSkills.size()));
  }
}
