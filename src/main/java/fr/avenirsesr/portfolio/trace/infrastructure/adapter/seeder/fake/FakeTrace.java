package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.programprogress.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.User;
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
            ELanguage.FALLBACK));
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
