package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.fake;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.CompetenceComplementaireDetaillee;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakeExternalSource;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class FakeAdditionalSkillProgress {
  private static final FakerProvider faker = new FakerProvider();
  private static final String JSON_PATH = "/mock/mock-additional-skills.json";
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final AdditionalSkillProgressEntity additionalSkillProgressEntity;

  private FakeAdditionalSkillProgress(AdditionalSkillProgressEntity additionalSkillProgressEntity) {
    this.additionalSkillProgressEntity = additionalSkillProgressEntity;
  }

  public static FakeAdditionalSkillProgress of(UserEntity student, List<UUID> bannedSkillsIds) {
    return new FakeAdditionalSkillProgress(
        AdditionalSkillProgressEntity.of(
            UUID.fromString(faker.call().internet().uuid()),
            student,
            getRandomIdByExternalSourceMockFile(bannedSkillsIds),
            faker.call().options().option(EAdditionalSkillLevel.class)));
  }

  public AdditionalSkillProgressEntity toEntity() {
    return additionalSkillProgressEntity;
  }

  private static UUID getRandomIdByExternalSourceMockFile(List<UUID> bannedIds) {
    try (InputStream is = FakeExternalSource.class.getResourceAsStream(JSON_PATH)) {
      List<CompetenceComplementaireDetaillee> entities =
          objectMapper.readValue(is, new TypeReference<>() {});

      List<CompetenceComplementaireDetaillee> filteredEntities =
          entities.stream().filter(entity -> !bannedIds.contains(entity.id())).toList();

      if (filteredEntities.isEmpty()) {
        throw new IllegalStateException("No available IDs to choose from: all are banned.");
      }

      CompetenceComplementaireDetaillee fakeObject =
          filteredEntities.get(faker.call().random().nextInt(filteredEntities.size()));
      return fakeObject.id();
    } catch (Exception e) {
      throw new RuntimeException("Unable to load mock additional skills", e);
    }
  }
}
