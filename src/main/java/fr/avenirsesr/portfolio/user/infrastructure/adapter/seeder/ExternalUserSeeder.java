package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.shared.infrastructure.utils.FileReader;
import fr.avenirsesr.portfolio.user.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.user.domain.port.input.ExternalUserService;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.ExternalUserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.ExternalUserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.ExternalUserCreationData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeExternalUser;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalUserSeeder {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(ExternalUserSeeder.class, SharedDataGenerator.class);

  private static final String PATH_FILE = "seeder/external-users.json";
  private final FileReader fileReader;
  private final ExternalUserService externalUserService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public List<ExternalUserEntity> seed(List<UserEntity> users) {
    log.info("Seeding External Users...");

    List<ExternalUserCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(
                  PATH_FILE, new TypeReference<List<ExternalUserCreationData>>() {});
          case FAKER ->
              users.stream()
                  .map(
                      user ->
                          FakeExternalUser.of(
                                  user,
                                  dataGenerator
                                      .with("external-user-category")
                                      .pickIn(EUserCategory.class))
                              .toEntity())
                  .map(
                      externalUser ->
                          new ExternalUserCreationData(
                              externalUser.getUser().getId(),
                              externalUser.getFirstName(),
                              externalUser.getLastName(),
                              externalUser.getEmail(),
                              externalUser.getCategory(),
                              externalUser.getExternalId(),
                              externalUser.getSource()))
                  .toList();
        };

    var externalUsers = new ArrayList<ExternalUser>();
    creationData.forEach(
        data -> {
          var externalUser =
              externalUserService.importExternalUser(
                  data.userId(),
                  data.firstName(),
                  data.lastName(),
                  data.email(),
                  data.category(),
                  data.externalId(),
                  data.source());
          externalUsers.add(externalUser);
        });

    return externalUsers.stream().map(ExternalUserMapper.INSTANCE::fromDomain).toList();
  }
}
