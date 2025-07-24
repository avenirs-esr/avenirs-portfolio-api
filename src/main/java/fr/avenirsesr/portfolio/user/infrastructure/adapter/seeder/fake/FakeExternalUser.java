package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakeExternalSource;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.ExternalUserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeExternalUser {
  private static final FakerProvider faker = new FakerProvider();
  private final ExternalUserEntity externalUser;

  private FakeExternalUser(ExternalUserEntity externalUser) {
    this.externalUser = externalUser;
  }

  public static FakeExternalUser of(UserEntity user, EUserCategory category) {
    if (category == EUserCategory.STUDENT
        && (user.getStudent().isEmpty() || !user.getStudent().get().isActive())) {
      throw new IllegalArgumentException("Student cannot be null");
    }
    if (category == EUserCategory.TEACHER
        && (user.getTeacher().isEmpty() || !user.getTeacher().get().isActive())) {
      throw new IllegalArgumentException("Student cannot be null");
    }

    return new FakeExternalUser(
        ExternalUserEntity.of(
            FakeExternalSource.generateExternalSourceId(),
            Arrays.stream(EExternalSource.values())
                .toList()
                .get(faker.call().random().nextInt(EExternalSource.values().length)),
            user,
            category,
            faker.call().internet().emailAddress(),
            user.getFirstName(),
            user.getLastName()));
  }

  public ExternalUserEntity toEntity() {
    return externalUser;
  }
}
