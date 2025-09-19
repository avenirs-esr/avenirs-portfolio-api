package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.SeederConfig;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.InstitutionDatabaseRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeInstitution;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstitutionSeeder {
  private final InstitutionDatabaseRepository institutionRepository;

  private FakeInstitution createFakeInstitution(Set<EPortfolioType> types) {
    FakeInstitution fakeInstitution = FakeInstitution.create().withEnabledFiled(types);

    Arrays.stream(ELanguage.values())
        .filter(language -> language != ELanguage.FRENCH)
        .forEach(fakeInstitution::addTranslation);

    return fakeInstitution;
  }

  @Transactional
  public List<InstitutionEntity> seed() {
    log.info("Seeding institutions...");

    List<FakeInstitution> fakeInstitutions = new ArrayList<>();
    for (int i = 0; i < SeederConfig.INSTITUTIONS_NB_OF_APC; i++) {
      fakeInstitutions.add(createFakeInstitution(Set.of(EPortfolioType.APC)));
    }
    for (int i = 0; i < SeederConfig.INSTITUTIONS_NB_OF_LIFE_PROJECT; i++) {
      fakeInstitutions.add(createFakeInstitution(Set.of(EPortfolioType.LIFE_PROJECT)));
    }
    for (int i = 0; i < SeederConfig.INSTITUTIONS_NB_OF_BOTH; i++) {
      fakeInstitutions.add(
          createFakeInstitution(Set.of(EPortfolioType.APC, EPortfolioType.LIFE_PROJECT)));
    }

    var institutionEntities = fakeInstitutions.stream().map(FakeInstitution::toEntity).toList();

    institutionRepository.saveAllEntities(institutionEntities);

    log.info(
        "✔ {} institutions created : {} APC - {} Life Project - {} Both",
        institutionEntities.size(),
        SeederConfig.INSTITUTIONS_NB_OF_APC,
        SeederConfig.INSTITUTIONS_NB_OF_LIFE_PROJECT,
        SeederConfig.INSTITUTIONS_NB_OF_BOTH);

    return institutionEntities;
  }
}
