package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
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
            EPortfolioType.APC));
  }

  public FakeProgram withLearningMethod(EPortfolioType learningMethod) {
    program.setLearningMethod(learningMethod);
    return this;
  }

  public Program toModel() {
    return program;
  }
}
