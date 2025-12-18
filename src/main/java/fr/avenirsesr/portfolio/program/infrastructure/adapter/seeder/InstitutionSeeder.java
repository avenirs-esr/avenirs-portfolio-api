package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.InstitutionDatabaseRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeInstitution;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstitutionSeeder {
  private final InstitutionDatabaseRepository institutionRepository;

  private FakeInstitution createFakeInstitution() {
    FakeInstitution fakeInstitution = FakeInstitution.create();

    Arrays.stream(ELanguage.values())
        .filter(language -> language != ELanguage.FRENCH)
        .forEach(fakeInstitution::addTranslation);

    return fakeInstitution;
  }

  @Transactional
  public List<InstitutionEntity> seed() {
    log.info("Seeding institutions...");

    int ALL_INSTITUTIONS_NB =
        SeederConfig.INSTITUTIONS_NB_OF_APC
            + SeederConfig.INSTITUTIONS_NB_OF_LIFE_PROJECT
            + SeederConfig.INSTITUTIONS_NB_OF_BOTH;
    List<FakeInstitution> fakeInstitutions = new ArrayList<>();
    for (int i = 0; i < ALL_INSTITUTIONS_NB; i++) {
      fakeInstitutions.add(createFakeInstitution());
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
