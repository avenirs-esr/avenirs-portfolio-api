package fr.avenirsesr.portfolio.api.domain.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillLevel {
  @Setter(AccessLevel.NONE)
  private final UUID id;

  @Setter(AccessLevel.NONE)
  private final String name;

  private ESkillLevelStatus status;
  private List<Track> tracks;
  private List<AMS> amses;
  private Skill skill;

  private SkillLevel(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  public static SkillLevel create(String name) {
    var skillLevel = new SkillLevel(UUID.randomUUID(), name);
    skillLevel.setStatus(ESkillLevelStatus.NOT_STARTED);
    skillLevel.setAmses(List.of());
    skillLevel.setTracks(List.of());

    return skillLevel;
  }

  public static SkillLevel toDomain(
      UUID id,
      String name,
      ESkillLevelStatus status,
      List<Track> tracks,
      List<AMS> amses,
      Skill skill) {
    var skillLevel = new SkillLevel(id, name);
    skillLevel.setStatus(status);
    skillLevel.setTracks(tracks);
    skillLevel.setAmses(amses);
    skillLevel.setSkill(skill);

    return skillLevel;
  }

  @Override
  public String toString() {
    return "SkillLevel[%s]".formatted(id);
  }
}
