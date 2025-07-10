package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trace {
  private final UUID id;
  private final User user;
  private String title;
  private List<SkillLevel> skillLevels;
  private List<AMS> amses;
  private Instant createdAt;
  private Instant updatedAt;
  private boolean isGroup;
  private ELanguage language;

  @Getter(AccessLevel.NONE)
  private Instant deletedAt;

  @Getter(AccessLevel.NONE)
  private String aiUseJustification;

  @Getter(AccessLevel.NONE)
  private String personalNote;

  private Trace(
      UUID id,
      User user,
      String title,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt,
      ELanguage language,
      List<SkillLevel> skillLevels,
      List<AMS> amses,
      boolean isGroup,
      String aiUseJustification,
      String personalNote) {
    this.id = id;
    this.user = user;
    this.title = title;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deletedAt = deletedAt;
    this.language = language;
    this.skillLevels = skillLevels;
    this.amses = amses;
    this.isGroup = isGroup;
    this.aiUseJustification = aiUseJustification;
    this.personalNote = personalNote;
  }

  public static Trace create(
      UUID id, User user, String title, Instant deletedAt, ELanguage language) {

    return new Trace(
        id,
        user,
        title,
        Instant.now(),
        null,
        deletedAt,
        language,
        List.of(),
        List.of(),
        false,
        null,
        null);
  }

  public static Trace toDomain(
      UUID id,
      User user,
      String title,
      List<SkillLevel> skillLevels,
      List<AMS> amses,
      boolean group,
      String aiUseJustification,
      String personalNote,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt,
      ELanguage language) {
    return new Trace(
        id,
        user,
        title,
        createdAt,
        updatedAt,
        deletedAt,
        language,
        skillLevels,
        amses,
        group,
        aiUseJustification,
        personalNote);
  }

  public Optional<Instant> getDeletedAt() {
    return Optional.ofNullable(deletedAt);
  }

  public Optional<String> getAiUseJustification() {
    return Optional.ofNullable(aiUseJustification);
  }

  public Optional<String> getPersonalNote() {
    return Optional.ofNullable(personalNote);
  }
}
