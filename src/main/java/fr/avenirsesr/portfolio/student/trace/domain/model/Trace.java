package fr.avenirsesr.portfolio.student.trace.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.student.trace.domain.model.enums.ETraceAuthorType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trace extends AvenirsBaseModel {
  private final User user;
  private String title;
  private ETraceAuthorType authorType;
  private ELanguage language;
  private boolean valorized;

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
      ETraceAuthorType authorType,
      String aiUseJustification,
      String personalNote,
      String link,
      File attachment,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.user = user;
    this.title = title;
    this.language = language;
    this.authorType = authorType;
    this.aiUseJustification = aiUseJustification;
    this.personalNote = personalNote;
    this.link = link;
    this.attachment = attachment;
    this.valorized = valorized;
  }

  public static Trace create(
      UUID id,
      User user,
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String aiUseJustification,
      String personalNote,
      String link,
      File attachment) {

    return new Trace(
        id,
        user,
        title,
        language,
        authorType,
        aiUseJustification,
        personalNote,
        link,
        attachment,
        false,
        Instant.now(),
        Instant.now());
  }

  public static Trace toDomain(
      UUID id,
      User user,
      String title,
      ETraceAuthorType authorType,
      String aiUseJustification,
      String personalNote,
      String link,
      File attachment,
      Instant createdAt,
      Instant updatedAt,
      ELanguage language,
      boolean valorized) {
    return new Trace(
        id,
        user,
        title,
        language,
        authorType,
        aiUseJustification,
        personalNote,
        link,
        attachment,
        valorized,
        createdAt,
        updatedAt);
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
