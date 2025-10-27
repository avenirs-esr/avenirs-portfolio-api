package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
<<<<<<< HEAD:src/main/java/fr/avenirsesr/portfolio/student/progress/infrastructure/adapter/seeder/fake/FakeAdditionalSkillProgress.java
=======
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
>>>>>>> 224d802f (feat(AdditionalSkill): check length of description field):src/main/java/fr/avenirsesr/portfolio/additionalskill/infrastructure/adapter/seeder/fake/FakeAdditionalSkillProgress.java
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
<<<<<<< HEAD:src/main/java/fr/avenirsesr/portfolio/student/progress/infrastructure/adapter/seeder/fake/FakeAdditionalSkillProgress.java
=======

  private static final DataGeneratorProvider<AdditionalSkillProgressDataGenerator>
      additionalSkillProgressGenerator =
          new DataGeneratorProvider<AdditionalSkillProgressDataGenerator>()
              .init(FakeAdditionalSkillProgress.class, AdditionalSkillProgressDataGenerator.class);

>>>>>>> 224d802f (feat(AdditionalSkill): check length of description field):src/main/java/fr/avenirsesr/portfolio/additionalskill/infrastructure/adapter/seeder/fake/FakeAdditionalSkillProgress.java
  private final AdditionalSkillProgressEntity additionalSkillProgressEntity;

  private FakeAdditionalSkillProgress(AdditionalSkillProgressEntity additionalSkillProgressEntity) {
    this.additionalSkillProgressEntity = additionalSkillProgressEntity;
  }

  public static FakeAdditionalSkillProgress of(
      StudentEntity student,
      List<AdditionalSkillEntity> savedAdditionalSkills,
      List<UUID> bannedSkillsIds) {
    return new FakeAdditionalSkillProgress(
        AdditionalSkillProgressEntity.of(
            dataGenerator.with("id").uuid(),
            student,
            getRandomAdditionalSkill(savedAdditionalSkills, bannedSkillsIds),
            dataGenerator.with("EAdditionalSkillLevel").pickIn(EAdditionalSkillLevel.class)));
  }

  public AdditionalSkillProgressEntity toEntity() {
    return additionalSkillProgressEntity;
  }

  private static AdditionalSkillEntity getRandomAdditionalSkill(
      List<AdditionalSkillEntity> savedAdditionalSkills, List<UUID> bannedIds) {

    if (savedAdditionalSkills.size() <= 2) {
      throw new IllegalStateException("The list must contain more than 2 items.");
    }

    List<AdditionalSkillEntity> allowedIds =
        savedAdditionalSkills.stream()
            .skip(2) // Do not attribute the first two elements for integration tests.
            .filter(additionalSkill -> !bannedIds.contains(additionalSkill.getId()))
            .toList();

    if (allowedIds.isEmpty()) {
      throw new IllegalStateException("No IDs available after excluding banned IDs.");
    }

    return allowedIds.get(
        dataGenerator.with("CompetenceComplementaireDetaillee").number(allowedIds.size()));
  }
}
