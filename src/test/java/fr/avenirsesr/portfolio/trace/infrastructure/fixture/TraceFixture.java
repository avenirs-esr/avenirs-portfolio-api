package fr.avenirsesr.portfolio.trace.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.FakeTrace;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.UUID;

public class TraceFixture {

  private UUID id;
  private User user;
  private String title;
  private Instant createdAt;
  private Instant updatedAt;
  private ETraceAuthorType authorType;
  private String aiUseJustification;
  private String personalNote;
  private String link;
  private File attachment;
  private ELanguage language = ELanguage.FRENCH;
  private boolean valorized;

  private TraceFixture() {
    var fakeUser = UserFixture.create().toModel();
    var base = FakeTrace.of(UserMapper.INSTANCE.fromDomain(fakeUser)).toEntity();
    this.id = base.getId();
    this.user = fakeUser;
    this.title = base.getTitle();
    this.createdAt = base.getCreatedAt();
    this.updatedAt = base.getUpdatedAt();
    this.authorType = base.getAuthorType();
    this.valorized = base.isValorized();
    this.attachment =
        File.create(
            UUID.randomUUID(), EFileType.PDF, "my fake pdf", 1000L, 1, "fake-url", user, false);
  }

  public static TraceFixture create() {
    return new TraceFixture();
  }

  public TraceFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public TraceFixture withUser(User user) {
    this.user = user;
    return this;
  }

  public TraceFixture withTitle(String title) {
    this.title = title;
    return this;
  }

  public TraceFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public TraceFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public TraceFixture withAuthorType(ETraceAuthorType authorType) {
    this.authorType = authorType;
    return this;
  }

  public TraceFixture withAiUseJustification(String aiUseJustification) {
    this.aiUseJustification = aiUseJustification;
    return this;
  }

  public TraceFixture withPersonalNote(String personalNote) {
    this.personalNote = personalNote;
    return this;
  }

  public TraceFixture withLink(String link) {
    this.link = link;
    return this;
  }

  public TraceFixture withAttachment(File attachment) {
    this.attachment = attachment;
    return this;
  }

  public TraceFixture withLanguage(ELanguage language) {
    this.language = language;
    return this;
  }

  public TraceFixture withValorized(boolean valorized) {
    this.valorized = valorized;
    return this;
  }

  public Trace toModel() {
    return Trace.toDomain(
        id,
        user,
        title,
        authorType,
        aiUseJustification,
        personalNote,
        link,
        attachment,
        createdAt,
        updatedAt,
        language,
        valorized);
  }
}
