package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
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
  private StudentProgress studentProgress;
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
      UUID id,
      User user,
      String title,
      Instant deletedAt,
      ELanguage language,
      StudentProgress studentProgress) {
    var trace = new Trace(id, user, title, Instant.now(), null, deletedAt, language);
    trace.setStudentProgress(studentProgress);
    trace.setAmses(List.of());
    trace.setGroup(false);

    return trace;
  }

  public static Trace toDomain(
      UUID id,
      User user,
      String title,
      StudentProgress studentProgress,
      List<AMS> amses,
      boolean group,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt,
      ELanguage language) {
    var trace = new Trace(id, user, title, createdAt, updatedAt, deletedAt, language);
    trace.setStudentProgress(studentProgress);
    trace.setAmses(amses);
    trace.setGroup(group);

    return trace;
  }

  public Optional<Instant> getDeletedAt() {
    return Optional.ofNullable(deletedAt);
  }
}
