package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.program.domain.model.Institution;
import fr.avenirsesr.portfolio.program.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.InstitutionMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.data.InstitutionCreationData;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeInstitution;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import java.util.ArrayList;
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
  private static final String PATH_FILE_FR = "seeder/programs.fr.json";
  private static final String PATH_FILE_EN = "seeder/programs.en.json";
  private final FileReader fileReader;
  private final InstitutionService institutionService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  private List<InstitutionCreationData> buildFakeInstitutions(ELanguage language) {
    return IntStream.range(0, SeederConfig.INSTITUTIONS_NB)
        .mapToObj(
            i -> {
              var tr = FakeInstitution.create();
              tr.addTranslation(language);
              return tr;
            })
        .map(FakeInstitution::toEntity)
        .map(
            institution ->
                new InstitutionCreationData(
                    institution.getId(),
                    institution.getTranslations().stream()
                        .filter(translationEntity -> translationEntity.getLanguage() == language)
                        .findFirst()
                        .orElseThrow()
                        .getName(),
                    List.of(),
                    List.of()))
        .toList();
  }

  @Transactional
  public List<InstitutionEntity> seed() {
    log.info("Seeding institutions...");

    List<InstitutionCreationData> creationDataFr =
        switch (seederSource) {
          case CSV -> fileReader.readJSON(PATH_FILE_FR, new TypeReference<>() {});
          case FAKER -> buildFakeInstitutions(ELanguage.FRENCH);
        };

    List<InstitutionCreationData> creationDataEn =
        switch (seederSource) {
          case CSV -> fileReader.readJSON(PATH_FILE_EN, new TypeReference<>() {});
          case FAKER -> buildFakeInstitutions(ELanguage.ENGLISH);
        };

    List<Institution> institutions = new ArrayList<>();

    creationDataFr.forEach(
        institutionData -> {
          RequestContext.set(new RequestData(Optional.empty(), ELanguage.FRENCH));
          var institution =
              institutionService.createInstitution(
                  institutionData.id(), institutionData.institution());
          creationDataEn.stream()
              .filter(i -> i.id().equals(institution.getId()))
              .findAny()
              .ifPresent(
                  en -> {
                    RequestContext.set(new RequestData(Optional.empty(), ELanguage.ENGLISH));
                    institutionService.updateInstitution(en.id(), en.institution());
                  });
          institutions.add(institution);
        });

    log.info("✔ {} institutions created", institutions.size());

    return institutions.stream().map(InstitutionMapper.INSTANCE::fromDomain).toList();
  }
}
