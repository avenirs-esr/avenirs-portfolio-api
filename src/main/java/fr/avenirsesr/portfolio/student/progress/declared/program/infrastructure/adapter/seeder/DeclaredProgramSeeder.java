package fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.input.DeclaredProgramService;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.seeder.data.DeclaredProgramCreationData;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.seeder.fake.FakeDeclaredProgram;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.util.*;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeclaredProgramSeeder {
  private static final String PATH_FILE = "seeder/declared-programs.json";
  private final FileReader fileReader;
  private final DeclaredProgramService declaredProgramService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public List<DeclaredProgramCreationData> seed(List<StudentEntity> savedStudents) {
    ValidationUtils.requireNonEmpty(savedStudents, "savedStudents cannot be empty");

    log.info("Seeding declared programs...");

    List<DeclaredProgramCreationData> creationDataList =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(
                  PATH_FILE, new TypeReference<List<DeclaredProgramCreationData>>() {});
          case FAKER ->
              IntStream.range(0, SeederConfig.NB_OF_DECLARED_PROGRAMS)
                  .mapToObj(i -> savedStudents.get(new Random().nextInt(savedStudents.size())))
                  .map(FakeDeclaredProgram::of)
                  .map(FakeDeclaredProgram::toEntity)
                  .map(
                      entity ->
                          new DeclaredProgramCreationData(
                              entity.getStudent().getId(),
                              entity.getTitle(),
                              entity.getStatus(),
                              entity.getDescription(),
                              entity.getOrganization(),
                              entity.getResult(),
                              entity.getSourceOfInformation(),
                              entity.getStartDate(),
                              entity.getEndDate()))
                  .toList();
        };

    creationDataList.forEach(
        creationData -> {
          declaredProgramService.create(
              creationData.studentId(),
              creationData.status(),
              creationData.title(),
              creationData.description(),
              creationData.organization(),
              creationData.result(),
              creationData.sourceOfInformation(),
              creationData.startDate(),
              creationData.endDate());
        });

    log.info("✔ {} declared programs created", creationDataList.size());
    return creationDataList;
  }
}
