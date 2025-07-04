package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevel;
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

  @Getter(AccessLevel.NONE)
  private Instant deletedAt;

  private boolean isGroup;
  private ELanguage language;

  private Trace(
      UUID id,
      User user,
      String title,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt,
      ELanguage language) {
    this.id = id;
    this.user = user;
    this.title = title;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deletedAt = deletedAt;
    this.language = language;
  }

  public static Trace create(
      UUID id, User user, String title, Instant deletedAt, ELanguage language) {
    var trace = new Trace(id, user, title, Instant.now(), null, deletedAt, language);
    trace.setSkillLevels(List.of());
    trace.setAmses(List.of());
    trace.setGroup(false);

    return trace;
  }

  public static Trace toDomain(
      UUID id,
      User user,
      String title,
      List<SkillLevel> skillLevels,
      List<AMS> amses,
      boolean group,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt,
      ELanguage language) {
    var trace = new Trace(id, user, title, createdAt, updatedAt, deletedAt, language);
    trace.setSkillLevels(skillLevels);
    trace.setAmses(amses);
    trace.setGroup(group);

    return trace;
  }

  public Optional<Instant> getDeletedAt() {
    return Optional.ofNullable(deletedAt);
  }
}
