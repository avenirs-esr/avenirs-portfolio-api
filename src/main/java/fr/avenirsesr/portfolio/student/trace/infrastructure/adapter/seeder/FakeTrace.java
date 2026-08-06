package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.student.trace.domain.model.enums.ETraceAuthorType;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
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

  public static FakeTrace of(StudentEntity student) {
    var fakeTrace =
        new FakeTrace(
            TraceEntity.of(
                UUID.randomUUID(),
                student,
                "Trace %s".formatted(faker().lorem().word()),
                ELanguage.FALLBACK,
                ETraceAuthorType.PERSONAL,
                null,
                null,
                null,
                null,
                false,
                Instant.now(),
                Instant.now()));

    ETraceAuthorType[] types = ETraceAuthorType.values();
    if (new Random().nextBoolean()) fakeTrace = fakeTrace.withAiUseJustification();
    if (new Random().nextBoolean()) fakeTrace = fakeTrace.withPersonalNote();
    if (new Random().nextBoolean()) fakeTrace = fakeTrace.withLink();
    if (new Random().nextBoolean())
      fakeTrace = fakeTrace.withAuthorType(types[new Random().nextInt(types.length)]);
    if (new Random().nextBoolean()) fakeTrace = fakeTrace.withValorized(true);

    return fakeTrace;
  }

  public FakeTrace withELanguage(ELanguage language) {
    trace.setLanguage(language);
    return this;
  }

  public FakeTrace withAuthorType(ETraceAuthorType authorType) {
    trace.setAuthorType(authorType);
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

  public FakeTrace withValorized(boolean valorized) {
    trace.setValorized(valorized);
    return this;
  }

  public TraceEntity toEntity() {
    return trace;
  }
}
