package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.repository.InstitutionDatabaseRepository;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake.FakeInstitution;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstitutionSeeder {
  private static final Faker faker = new FakerProvider().call();
  private static final int NB_INSTITUTIONS = 5;
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

    Arrays.stream(ELanguage.values())
        .filter(language -> language != ELanguage.FRENCH)
        .forEach(fakeInstitution::addTranslation);

    return fakeInstitution;
  }

  public List<InstitutionEntity> seed() {
    log.info("Seeding institutions...");

    List<FakeInstitution> fakeInstitutions = new ArrayList<>();
    for (int i = 0; i < NB_INSTITUTIONS; i++) {
      fakeInstitutions.add(createFakeInstitution());
    }

    var institutionEntities = fakeInstitutions.stream().map(FakeInstitution::toEntity).toList();

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
