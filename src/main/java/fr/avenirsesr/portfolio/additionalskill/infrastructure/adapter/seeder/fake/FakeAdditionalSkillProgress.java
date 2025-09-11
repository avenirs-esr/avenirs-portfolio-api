package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.CompetenceComplementaireDetaillee;
import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.SharedDataGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.FakeExternalSource;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class FakeAdditionalSkillProgress {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeAdditionalSkillProgress.class, SharedDataGenerator.class);
  private final AdditionalSkillProgressEntity additionalSkillProgressEntity;

  private FakeAdditionalSkillProgress(AdditionalSkillProgressEntity additionalSkillProgressEntity) {
    this.additionalSkillProgressEntity = additionalSkillProgressEntity;
  }

  public static FakeAdditionalSkillProgress of(
      UserEntity student,
      List<AdditionalSkillEntity> savedAdditionalSkills,
      List<UUID> bannedSkillsIds) {
    return new FakeAdditionalSkillProgress(
        AdditionalSkillProgressEntity.of(
            dataGenerator.with("id").uuid(),
            student,
                getRandomAdditionalSkillId(savedAdditionalSkills, bannedSkillsIds),
                dataGenerator.with("EAdditionalSkillLevel").pickIn(EAdditionalSkillLevel.class)));
  }

  public AdditionalSkillProgressEntity toEntity() {
    return additionalSkillProgressEntity;
  }

  private static UUID getRandomAdditionalSkillId(
      List<AdditionalSkillEntity> savedAdditionalSkills, List<UUID> bannedIds) {

    if (savedAdditionalSkills.size() <= 2) {
      throw new IllegalStateException("The list must contain more than 2 items.");
    }

    List<UUID> allowedIds =
        savedAdditionalSkills.stream()
            .skip(2) // Do not attribute the first two elements for integration tests.
            .map(AdditionalSkillEntity::getId)
            .filter(id -> !bannedIds.contains(id))
            .toList();

    if (allowedIds.isEmpty()) {
      throw new IllegalStateException("No IDs available after excluding banned IDs.");
    }

    return allowedIds.get(
            dataGenerator
                    .with("CompetenceComplementaireDetaillee").random().nextInt(allowedIds.size()));
  }
}
