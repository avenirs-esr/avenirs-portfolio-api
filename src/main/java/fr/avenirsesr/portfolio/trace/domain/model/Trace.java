package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
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
public class Trace extends AvenirsBaseModel {
  private final User user;
  private String title;
  private List<SkillLevelProgress> skillLevels;
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
      ELanguage language,
      boolean isGroup,
      String aiUseJustification,
      String personalNote,
      List<SkillLevelProgress> skillLevels,
      List<AMS> amses,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt) {
    super(id);
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
      UUID id,
      User user,
      String title,
      ELanguage language,
      boolean isGroup,
      String aiUseJustification,
      String personalNote) {

    return new Trace(
        id,
        user,
        title,
        language,
        isGroup,
        aiUseJustification,
        personalNote,
        List.of(),
        List.of(),
        Instant.now(),
        Instant.now(),
        null);
  }

  public static Trace toDomain(
      UUID id,
      User user,
      String title,
      List<SkillLevelProgress> skillLevels,
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
        language,
        group,
        aiUseJustification,
        personalNote,
        skillLevels,
        amses,
        createdAt,
        updatedAt,
        deletedAt);
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
