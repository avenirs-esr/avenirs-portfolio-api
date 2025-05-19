package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import java.util.Arrays;
import net.datafaker.Faker;

public class FakeExternalUser {
  private static final Faker faker = new Faker();
  private final ExternalUser externalUser;

  private FakeExternalUser(ExternalUser externalUser) {
    this.externalUser = externalUser;
  }

  public static FakeExternalUser of(User user, EUserCategory category) {
    if (category == EUserCategory.STUDENT && user.getStudent() == null) {
      throw new IllegalArgumentException("Student cannot be null");
    }
    if (category == EUserCategory.TEACHER && user.getTeacher() == null) {
      throw new IllegalArgumentException("Student cannot be null");
    }

    return new FakeExternalUser(
        ExternalUser.create(
            user,
            generateRandomExternalId(),
            Arrays.stream(EExternalSource.values())
                .toList()
                .get(faker.random().nextInt(EExternalSource.values().length)),
            category,
            faker.internet().emailAddress(),
            user.getFirstName(),
            user.getLastName()));
  }

  private static String generateRandomExternalId() {
    int idType = faker.number().numberBetween(0, 3);

    return switch (idType) {
      case 0 -> faker.internet().uuid();
      case 1 -> String.valueOf(faker.number().numberBetween(1, 999_999));
      case 2 -> faker.regexify("[A-Z]{3}[0-9]{3}");
      default -> throw new IllegalStateException("Unexpected value: " + idType);
    };
  }

  public ExternalUser toModel() {
    return externalUser;
  }
}
