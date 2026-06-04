package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import net.datafaker.Faker;

public class FakeTrace {
  private final TraceEntity trace;

  private static Faker faker() {
    return new Faker();
  }

  private FakeTrace(TraceEntity trace) {
    this.trace = trace;
  }

  public static FakeTrace of(UserEntity user) {
    var fakeTrace =
        new FakeTrace(
            TraceEntity.of(
                UUID.randomUUID(),
                user,
                "Trace %s".formatted(faker().lorem().word()),
                ELanguage.FALLBACK,
                ETraceAuthorType.PERSONAL,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now(),
                null));

    ETraceAuthorType[] types = ETraceAuthorType.values();
    if (new Random().nextBoolean()) fakeTrace = fakeTrace.withAiUseJustification();
    if (new Random().nextBoolean()) fakeTrace = fakeTrace.withPersonalNote();
    if (new Random().nextBoolean()) fakeTrace = fakeTrace.withLink();
    if (new Random().nextBoolean())
      fakeTrace = fakeTrace.withTraceAuthorType(types[new Random().nextInt(types.length)]);

    return fakeTrace;
  }

  public FakeTrace withELanguage(ELanguage language) {
    trace.setLanguage(language);
    return this;
  }

  public FakeTrace withDeletedAt(Instant deletedAt) {
    trace.setDeletedAt(deletedAt);
    return this;
  }

  public FakeTrace withTraceAuthorType(ETraceAuthorType traceAuthorType) {
    trace.setTraceAuthorType(traceAuthorType);
    return this;
  }

  public FakeTrace withAiUseJustification() {
    trace.setAiUseJustification("I use AI for : %s".formatted(faker().lorem().sentence(5)));
    return this;
  }

  public FakeTrace withPersonalNote() {
    trace.setPersonalNote("Personal note : %s".formatted(faker().lorem().sentence(5)));
    return this;
  }

  public FakeTrace withLink() {
    trace.setLink(faker().internet().url());
    return this;
  }

  public TraceEntity toEntity() {
    return trace;
  }
}
