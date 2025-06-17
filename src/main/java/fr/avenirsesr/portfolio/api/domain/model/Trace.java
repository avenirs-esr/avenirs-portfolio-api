package fr.avenirsesr.portfolio.api.domain.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
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
  private Instant deletionDate;
  private boolean isGroup;
  private ELanguage language;

  private Trace(
      UUID id,
      User user,
      String title,
      Instant createdAt,
      Instant updatedAt,
      Instant deletionDate,
      ELanguage language) {
    this.id = id;
    this.user = user;
    this.title = title;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deletionDate = deletionDate;
    this.language = language;
  }

  public static Trace create(
      UUID id, User user, String title, Instant deletionDate, ELanguage language) {
    var trace = new Trace(id, user, title, Instant.now(), null, deletionDate, language);
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
      Instant deletionDate,
      ELanguage language) {
    var trace = new Trace(id, user, title, createdAt, updatedAt, deletionDate, language);
    trace.setSkillLevels(skillLevels);
    trace.setAmses(amses);
    trace.setGroup(group);

    return trace;
  }
}
