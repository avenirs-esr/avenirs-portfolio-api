package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FakeTrace {
  private static final FakerProvider faker = new FakerProvider();
  private final Trace trace;

  private FakeTrace(Trace trace) {
    this.trace = trace;
  }

  public static FakeTrace of(User user) {
    return new FakeTrace(
        Trace.create(
            UUID.fromString(faker.call().internet().uuid()),
            user,
            faker.call().lorem().sentence(),
            Instant.now().plus(90, ChronoUnit.DAYS),
            ELanguage.FRENCH));
  }

  public FakeTrace withSkillLevel(List<SkillLevel> skillLevels) {
    trace.setSkillLevels(skillLevels);
    skillLevels.forEach(
        skillLevel ->
            skillLevel.setTraces(
                Stream.concat(skillLevel.getTraces().stream(), Stream.of(trace)).toList()));
    return this;
  }

  public FakeTrace withAMS(List<AMS> amses) {
    trace.setAmses(amses);
    return this;
  }

  public Trace toModel() {
    return trace;
  }
}
