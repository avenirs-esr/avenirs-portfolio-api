package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.domain.model.Program;
import java.util.UUID;

public class FakeProgram {
  private static final FakerProvider faker = new FakerProvider();
  private final Program program;

  private FakeProgram(Program program) {
    this.program = program;
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
            institution.getLanguage()));
  }

  public FakeProgram isNotAPC() {
    program.setAPC(false);
    return this;
  }

  public static String createName() {
    return "%s %s - %s"
        .formatted(
            faker.call().university().prefix(),
            faker.call().university().degree(),
            faker.call().number().numberBetween(1, 11));
  }

  public Program toModel() {
    return program;
  }
}
