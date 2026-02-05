package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.program.domain.model.Institution;
import fr.avenirsesr.portfolio.program.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.InstitutionMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.data.InstitutionCreationData;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeInstitution;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.utils.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstitutionSeeder {
  private static final String PATH_FILE = "seeder/programs.fr.json";
  private final FileReader fileReader;
  private final InstitutionService institutionService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

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

    List<InstitutionCreationData> creationData =
        switch (seederSource) {
          case CSV -> fileReader.readJSON(PATH_FILE, new TypeReference<>() {});
          case FAKER ->
              IntStream.range(0, SeederConfig.INSTITUTIONS_NB)
                  .mapToObj(i -> createFakeInstitution())
                  .map(
                      institution ->
                          new InstitutionCreationData(
                              institution.toEntity().getTranslations().stream()
                                  .filter(
                                      translationEntity ->
                                          translationEntity.getLanguage() == ELanguage.FRENCH)
                                  .findFirst()
                                  .orElseThrow()
                                  .getName(),
                              List.of(),
                              List.of()))
                  .toList();
        };

    List<Institution> institutions = new ArrayList<>();

    creationData.forEach(
        institutionData -> {
          RequestContext.set(new RequestData(Optional.empty(), ELanguage.FRENCH));
          var institution = institutionService.createInstitution(institutionData.institution());
          institutions.add(institution);
        });

    log.info("✔ {} institutions created", institutions.size());

    return institutions.stream().map(InstitutionMapper.INSTANCE::fromDomain).toList();
  }
}
