package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.api.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeExternalUser {
  private static final FakerProvider faker = new FakerProvider();
  private final ExternalUser externalUser;

  private FakeExternalUser(ExternalUser externalUser) {
    this.externalUser = externalUser;
  }

  public static FakeExternalUser of(User user, EUserCategory category) {
    if (category == EUserCategory.STUDENT && !user.isStudent()) {
      throw new IllegalArgumentException("Student cannot be null");
    }
    if (category == EUserCategory.TEACHER && !user.isTeacher()) {
      throw new IllegalArgumentException("Student cannot be null");
    }

    int externalIdType = faker.call().number().numberBetween(0, 3);

    return new FakeExternalUser(
        ExternalUser.create(
            user,
            switch (externalIdType) {
              case 0 -> faker.call().internet().uuid();
              case 1 -> String.valueOf(faker.call().number().numberBetween(1, 999_999));
              case 2 -> faker.call().regexify("[A-Z]{3}[0-9]{3}");
              default -> throw new IllegalStateException("Unexpected value: " + externalIdType);
            },
            Arrays.stream(EExternalSource.values())
                .toList()
                .get(faker.call().random().nextInt(EExternalSource.values().length)),
            category,
            faker.call().internet().emailAddress(),
            user.getFirstName(),
            user.getLastName()));
  }

  public ExternalUser toModel() {
    return externalUser;
  }
}
