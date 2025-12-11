package fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.seeder.fake;

import static org.apache.commons.lang3.StringUtils.truncate;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.model.DeclaredProgramEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Random;
import net.datafaker.Faker;

public class FakeDeclaredProgram {

  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeDeclaredProgram.class, SharedDataGenerator.class);

  private static final Faker faker = new Faker();
  private static final Random random = new Random();

  private final DeclaredProgramEntity declaredProgram;

  private FakeDeclaredProgram(DeclaredProgramEntity declaredProgram) {
    this.declaredProgram = declaredProgram;
  }

  public static FakeDeclaredProgram of(StudentEntity student) {
    LocalDate startDate = LocalDate.now().minusMonths(3 + random.nextInt(9));
    LocalDate endDate = random.nextBoolean() ? null : startDate.plusMonths(3 + random.nextInt(6));

    return new FakeDeclaredProgram(
        DeclaredProgramEntity.of(
            dataGenerator.with("id").uuid(),
            student,
            dataGenerator.with("EProgramStatus").pickIn(EProgramStatus.class),
            "Program - %s".formatted(truncate(faker.lorem().sentence(3), 60)),
            "Description - %s".formatted(truncate(faker.lorem().sentence(10), 350)),
            "Organization - %s".formatted(truncate(faker.lorem().sentence(1), 30)),
            "Result - %s".formatted(truncate(faker.lorem().sentence(1), 10)),
            "Source - %s".formatted(truncate(faker.lorem().sentence(3), 100)),
            faker.internet().url(),
            startDate,
            endDate,
            Instant.now(),
            Instant.now()));
  }

  public DeclaredProgramEntity toEntity() {
    return declaredProgram;
  }
}
