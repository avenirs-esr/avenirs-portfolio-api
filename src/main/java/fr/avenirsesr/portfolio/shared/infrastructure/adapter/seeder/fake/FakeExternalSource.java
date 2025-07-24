package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.CompetenceComplementaireDetaillee;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class FakeExternalSource {
  private static final FakerProvider faker = new FakerProvider();
  private static final String JSON_PATH = "/mock/mock-additional-skills.json";
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String generateExternalSourceId() {
    int externalIdType = faker.call().number().numberBetween(0, 3);
    return switch (externalIdType) {
      case 0 -> faker.call().internet().uuid();
      case 1 -> String.valueOf(faker.call().number().numberBetween(1, 999_999));
      case 2 -> faker.call().regexify("[A-Z]{3}[0-9]{3}");
      default -> throw new IllegalStateException("Unexpected value: " + externalIdType);
    };
  }

  public static UUID getRandomIdByExternalSourceMockFile() {
    return getRandomIdByExternalSourceMockFile(List.of());
  }

  public static UUID getRandomIdByExternalSourceMockFile(List<UUID> bannedIds) {
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
