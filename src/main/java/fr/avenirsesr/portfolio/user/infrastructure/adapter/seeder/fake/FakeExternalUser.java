package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.user.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.user.domain.port.output.seeder.UserDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.ExternalUserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeExternalUser {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeExternalUser.class, SharedDataGenerator.class);

  private static final DataGeneratorProvider<UserDataGenerator> userDataGenerator =
      new DataGeneratorProvider<UserDataGenerator>()
          .init(FakeExternalUser.class, UserDataGenerator.class);
  private final ExternalUserEntity externalUser;

  private FakeExternalUser(ExternalUserEntity externalUser) {
    this.externalUser = externalUser;
  }

  public static FakeExternalUser of(UserEntity user, EUserCategory category) {
    return new FakeExternalUser(
        ExternalUserEntity.of(
            dataGenerator.with("id").uuid(),
            dataGenerator.with("external-id").externalId(),
            dataGenerator.with("EExternalSource").pickIn(EExternalSource.class),
            user,
            category,
            userDataGenerator.with("email").email(),
            user.getFirstName(),
            user.getLastName(),
            Instant.now(),
            Instant.now()));
  }

  public ExternalUserEntity toEntity() {
    return externalUser;
  }
}
