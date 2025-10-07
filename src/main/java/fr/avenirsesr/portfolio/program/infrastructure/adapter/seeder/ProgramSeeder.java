package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.ProgramDatabaseRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeProgram;
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
public class ProgramSeeder {

  private final ProgramDatabaseRepository programRepository;

  private FakeProgram createFakeProgram(InstitutionEntity institution, boolean isApc) {
    var fakeProgram = FakeProgram.of(institution);
    if (!isApc) {
      fakeProgram = fakeProgram.isNotAPC();
    }

    Arrays.stream(ELanguage.values())
        .filter(language -> language != ELanguage.FRENCH)
        .forEach(fakeProgram::addTranslation);

    return fakeProgram;
  }

  @Transactional
  public List<ProgramEntity> seed(List<InstitutionEntity> institutionEntities) {
    ValidationUtils.requireNonEmpty(institutionEntities, "institutions cannot be empty");

    log.info("Seeding programs...");

    List<FakeProgram> fakePrograms = new ArrayList<>();

    institutionEntities.forEach(
        institution -> {
          for (int i = 0; i < SeederConfig.PROGRAM_BY_INSTITUTION; i++) {
            fakePrograms.add(
                createFakeProgram(institutionEntities.get(i), i < SeederConfig.PROGRAM_NB_APC));
          }
        });

    var programEntities = fakePrograms.stream().map(FakeProgram::toEntity).toList();

    programRepository.saveAllEntities(programEntities);

    log.info(
        "✔ {} program created by institution -> Total {} Programs created",
        SeederConfig.PROGRAM_BY_INSTITUTION,
        programEntities.size());

    return programEntities;
  }
}
