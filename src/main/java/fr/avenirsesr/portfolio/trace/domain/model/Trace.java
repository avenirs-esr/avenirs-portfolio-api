package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.DeletableAvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trace extends DeletableAvenirsBaseModel {
  private final User user;
  private String title;
  private ETraceAuthorType traceAuthorType;
  private ELanguage language;

  @Getter(AccessLevel.NONE)
  private String aiUseJustification;

  @Getter(AccessLevel.NONE)
  private String personalNote;

  @Getter(AccessLevel.NONE)
  private File attachment;

  @Getter(AccessLevel.NONE)
  private String link;

  private Trace(
      UUID id,
      User user,
      String title,
      ELanguage language,
      ETraceAuthorType traceAuthorType,
      String aiUseJustification,
      String personalNote,
      String link,
      File attachment,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt) {
    super(id, deletedAt, createdAt, updatedAt);
    this.user = user;
    this.title = title;
    this.language = language;
    this.traceAuthorType = traceAuthorType;
    this.aiUseJustification = aiUseJustification;
    this.personalNote = personalNote;
    this.link = link;
    this.attachment = attachment;
  }

  public static Trace create(
      UUID id,
      User user,
      String title,
      ELanguage language,
      ETraceAuthorType traceAuthorType,
      String aiUseJustification,
      String personalNote,
      String link,
      File attachment) {

    return new Trace(
        id,
        user,
        title,
        language,
        traceAuthorType,
        aiUseJustification,
        personalNote,
        link,
        attachment,
        Instant.now(),
        Instant.now(),
        null);
  }

  public static Trace toDomain(
      UUID id,
      User user,
      String title,
      ETraceAuthorType traceAuthorType,
      String aiUseJustification,
      String personalNote,
      String link,
      File attachment,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt,
      ELanguage language) {
    return new Trace(
        id,
        user,
        title,
        language,
        traceAuthorType,
        aiUseJustification,
        personalNote,
        link,
        attachment,
        createdAt,
        updatedAt,
        deletedAt);
  }

  public Optional<String> getAiUseJustification() {
    return Optional.ofNullable(aiUseJustification);
  }

  public Optional<String> getPersonalNote() {
    return Optional.ofNullable(personalNote);
  }

  public Optional<String> getLink() {
    return Optional.ofNullable(link);
  }

  public Optional<File> getAttachment() {
    return Optional.ofNullable(attachment);
  }
}
