package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakeExternalSource;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.List;
import java.util.UUID;

public class FakeAdditionalSkillProgress {
  private static final FakerProvider faker = new FakerProvider();
  private final AdditionalSkillProgressEntity additionalSkillProgressEntity;

  private FakeAdditionalSkillProgress(AdditionalSkillProgressEntity additionalSkillProgressEntity) {
    this.additionalSkillProgressEntity = additionalSkillProgressEntity;
  }

  public static FakeAdditionalSkillProgress of(UserEntity student, List<UUID> bannedSkillsIds) {
    return new FakeAdditionalSkillProgress(
        AdditionalSkillProgressEntity.of(
            UUID.fromString(faker.call().internet().uuid()),
            student,
            FakeExternalSource.getRandomIdByExternalSourceMockFile(bannedSkillsIds),
            faker.call().options().option(EAdditionalSkillLevel.class)));
  }

  public AdditionalSkillProgressEntity toEntity() {
    return additionalSkillProgressEntity;
  }
}
