package fr.avenirsesr.portfolio.api.domain.model;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AMS {
  @Setter(AccessLevel.NONE)
  private final UUID id;

  @Setter(AccessLevel.NONE)
  private final User user;

  private List<SkillLevel> skillLevels;

  private AMS(UUID id, User user) {
    this.id = id;
    this.user = user;
  }

  public static AMS create(User user) {
    var ams = new AMS(UUID.randomUUID(), user);
    ams.setSkillLevels(List.of());

    return ams;
  }

  public static AMS toDomain(UUID id, User user, List<SkillLevel> skillLevels) {
    var ams = new AMS(id, user);
    ams.setSkillLevels(skillLevels);

    return ams;
  }
}
