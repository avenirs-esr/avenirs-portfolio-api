package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.user.domain.port.output.seeder.UserDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;

public class FakeUser {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeUser.class, SharedDataGenerator.class);

  private static final DataGeneratorProvider<UserDataGenerator> userDataGenerator =
      new DataGeneratorProvider<UserDataGenerator>().init(FakeUser.class, UserDataGenerator.class);
  private final UserEntity user;

  private FakeUser(UserEntity user) {
    this.user = user;
  }

  public static FakeUser create() {
    return new FakeUser(
        UserEntity.of(
            dataGenerator.with("id").uuid(),
            userDataGenerator.with("firstName").firstName(),
            userDataGenerator.with("lastName").lastName(),
            userDataGenerator.with("email").email(),
            false,
            Instant.now(),
            Instant.now()));
  }

  public FakeUser withEmail(String email) {
    user.setEmail(email);
    return this;
  }

  public UserEntity toEntity() {
    return user;
  }
}
