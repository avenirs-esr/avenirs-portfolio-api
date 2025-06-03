package fr.avenirsesr.portfolio.api.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Track {
  private final UUID id;
  private final User user;
  private String title;
  private List<SkillLevel> skillLevels;
  private List<AMS> amses;
  private Instant createdAt;
  private boolean isGroup;

  private Track(UUID id, User user, String title, Instant createdAt) {
    this.id = id;
    this.user = user;
    this.title = title;
    this.createdAt = createdAt;
  }

  public static Track create(UUID id, User user, String title) {
    var track = new Track(id, user, title, Instant.now());
    track.setSkillLevels(List.of());
    track.setAmses(List.of());
    track.setGroup(false);

    return track;
  }

  public static Track toDomain(
      UUID id,
      User user,
      String title,
      List<SkillLevel> skillLevels,
      List<AMS> amses,
      boolean group,
      Instant createdAt) {
    var track = new Track(id, user, title, createdAt);
    track.setSkillLevels(skillLevels);
    track.setAmses(amses);
    track.setGroup(group);

    return track;
  }
}
