package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.repository.ProgramDatabaseRepository;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake.FakeProgram;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramSeeder {
  private static final int NB_PROGRAM_BY_INSTITUTION = 3;
  private static final int NB_OF_APC = 2;

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

  public List<ProgramEntity> seed(List<InstitutionEntity> institutionEntities) {
    if (institutionEntities == null || institutionEntities.isEmpty()) {
      throw new IllegalArgumentException("The list of institutions is empty");
    }

    log.info("Seeding programs...");

    List<FakeProgram> fakePrograms = new ArrayList<>();

    institutionEntities.forEach(
        institution -> {
          for (int i = 0; i < NB_PROGRAM_BY_INSTITUTION; i++) {
            fakePrograms.add(createFakeProgram(institutionEntities.get(i), i < NB_OF_APC));
          }
        });

    var programEntities = fakePrograms.stream().map(FakeProgram::toEntity).toList();

    programRepository.saveAllEntities(programEntities);

    log.info(
        "✓ {} program created by institution -> Total {} Programs created",
        NB_PROGRAM_BY_INSTITUTION,
        programEntities.size());

    return programEntities;
  }
}
