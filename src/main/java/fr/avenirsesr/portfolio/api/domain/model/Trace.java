package fr.avenirsesr.portfolio.api.domain.model;

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
  private boolean isGroup;

  private Trace(UUID id, User user, String title, Instant createdAt) {
    this.id = id;
    this.user = user;
    this.title = title;
    this.createdAt = createdAt;
  }

  public static Trace create(UUID id, User user, String title) {
    var trace = new Trace(id, user, title, Instant.now());
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
      Instant createdAt) {
    var trace = new Trace(id, user, title, createdAt);
    trace.setSkillLevels(skillLevels);
    trace.setAmses(amses);
    trace.setGroup(group);

    return trace;
  }
}
