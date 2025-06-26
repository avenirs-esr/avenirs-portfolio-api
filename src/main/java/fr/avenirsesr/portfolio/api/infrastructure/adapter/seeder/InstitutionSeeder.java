package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionTranslationEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.repository.InstitutionDatabaseRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake.FakeInstitution;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake.FakerProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstitutionSeeder {
  private static final Faker faker = new FakerProvider().call();
  private static final int NB_INSTITUTIONS = 30;
  private static final double PROBABILITY_OF_APC = 0.1;
  private static final double PROBABILITY_OF_LIFE_PROJECT = 0.1;
  private static final double PROBABILITY_OF_BOTH_TYPE = 0.8;

  private int nbApc = 0;
  private int nbLifeProject = 0;
  private int nbBothType = 0;

  private final InstitutionDatabaseRepository institutionRepository;

  private FakeInstitution createFakeInstitution() {
    FakeInstitution fakeInstitution = FakeInstitution.create();

    int typeRandom = faker.random().nextInt(1, 10);

    if (typeRandom == PROBABILITY_OF_APC * 10) {
      fakeInstitution.withEnabledFiled(Set.of(EPortfolioType.APC));
      nbApc++;
    } else if (typeRandom == (PROBABILITY_OF_APC + PROBABILITY_OF_LIFE_PROJECT) * 10) {
      fakeInstitution.withEnabledFiled(Set.of(EPortfolioType.LIFE_PROJECT));
      nbLifeProject++;
    } else nbBothType++;

    var availableLanguages =
        new ArrayList<>(
            Arrays.stream(ELanguage.values())
                .filter(language -> language != ELanguage.FRENCH)
                .toList());
    Collections.shuffle(availableLanguages);

    for (int i = 0; i < faker.random().nextInt(availableLanguages.size() + 1); i++) {
      fakeInstitution.addTranslation(availableLanguages.get(i));
    }

    return fakeInstitution;
  }

  public List<InstitutionEntity> seed() {
    log.info("Seeding institutions...");

    List<FakeInstitution> fakeInstitutions = new ArrayList<>();
    for (int i = 0; i < NB_INSTITUTIONS; i++) {
      fakeInstitutions.add(createFakeInstitution());
    }

    var institutionEntities =
        fakeInstitutions.stream()
            .map(
                fakeInstitution -> {
                  InstitutionEntity institutionEntity =
                      new InstitutionEntity(
                          fakeInstitution.toModel().getId(),
                          fakeInstitution.toModel().getEnabledFields());

                  var translations =
                      fakeInstitution.getTranslations().stream()
                          .map(
                              t ->
                                  new InstitutionTranslationEntity(
                                      UUID.fromString(faker.internet().uuid()), t.language(), t.name(), institutionEntity))
                          .collect(Collectors.toSet());

                  institutionEntity.setTranslations(translations);
                  return institutionEntity;
                })
            .toList();

    institutionRepository.saveAllEntities(institutionEntities);
    log.info(
        "✓ {} institutions created : {} APC - {} Life Project - {} Both",
        institutionEntities.size(),
        nbApc,
        nbLifeProject,
        nbBothType);

    return institutionEntities;
  }
}
