package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.domain.model.Program;
import net.datafaker.Faker;

public class FakeProgram {
  private static final Faker faker = new Faker();
  private final Program program;

  private FakeProgram(Program program) {
    this.program = program;
  }

  public static FakeProgram of(Institution institution) {
    return new FakeProgram(
        Program.create(
            institution,
            "%s %s - %s"
                .formatted(
                    faker.university().prefix(),
                    faker.university().degree(),
                    faker.number().numberBetween(1, 11)),
            true));
  }

  public FakeProgram isNotAPC() {
    program.setAPC(false);
    return this;
  }

  public Program toModel() {
    return program;
  }
}
