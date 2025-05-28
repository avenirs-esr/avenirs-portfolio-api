package fr.avenirsesr.portfolio.api.domain.model;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Track {
  @Setter(AccessLevel.NONE)
  private final UUID id;

  @Setter(AccessLevel.NONE)
  private final User user;

  private List<SkillLevel> skillLevels;
  private List<AMS> amses;

  private Track(UUID id, User user) {
    this.id = id;
    this.user = user;
  }

  public static Track create(UUID id, User user) {
    var track = new Track(id, user);
    track.setSkillLevels(List.of());
    track.setAmses(List.of());

    return track;
  }

  public static Track toDomain(UUID id, User user, List<SkillLevel> skillLevels, List<AMS> amses) {
    var track = new Track(id, user);
    track.setSkillLevels(skillLevels);
    track.setAmses(amses);

    return track;
  }
}
