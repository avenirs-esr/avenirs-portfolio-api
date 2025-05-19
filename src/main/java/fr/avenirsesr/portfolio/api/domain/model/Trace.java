package fr.avenirsesr.portfolio.api.domain.model;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trace {
  @Setter(AccessLevel.NONE)
  private final UUID id;

  @Setter(AccessLevel.NONE)
  private final User user;

  private List<SkillLevel> skillLevels;
  private List<AMS> amses;

  private Trace(UUID id, User user) {
    this.id = id;
    this.user = user;
  }

  public static Trace create(User user) {
    var trace = new Trace(UUID.randomUUID(), user);
    trace.setSkillLevels(List.of());
    trace.setAmses(List.of());

    return trace;
  }

  public static Trace toDomain(UUID id, User user, List<SkillLevel> skillLevels, List<AMS> amses) {
    var trace = new Trace(id, user);
    trace.setSkillLevels(skillLevels);
    trace.setAmses(amses);

    return trace;
  }
}
