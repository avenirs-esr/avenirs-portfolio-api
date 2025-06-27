package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.domain.model.enums.EDurationUnit;
import java.util.UUID;

public class FakeProgram {
  private static final FakerProvider faker = new FakerProvider();
  private final Program program;

  private FakeProgram(Program program) {
    this.program = program;
  }

  private static EDurationUnit randomDurationUnit() {
    return faker.call().options().option(EDurationUnit.values());
  }

  public static FakeProgram of(Institution institution) {
    return new FakeProgram(
        Program.create(
            UUID.fromString(faker.call().internet().uuid()),
            institution,
            "%s %s - %s"
                .formatted(
                    faker.call().university().prefix(),
                    faker.call().university().degree(),
                    faker.call().number().numberBetween(1, 11)),
            true,
            randomDurationUnit(),
            faker.call().number().numberBetween(1, 5)));
  }

  public static String createName() {
    return "%s %s - %s"
        .formatted(
            faker.call().university().prefix(),
            faker.call().university().degree(),
            faker.call().number().numberBetween(1, 11));
  }

  public FakeProgram isNotAPC() {
    program.setAPC(false);
    return this;
  }

  public Program toModel() {
    return program;
  }
}
