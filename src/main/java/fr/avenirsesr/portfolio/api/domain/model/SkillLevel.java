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
  private List<Trace> traces;
  private List<AMS> amses;
  private Skill skill;

  private SkillLevel(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  public static SkillLevel create(UUID id, String name) {
    var skillLevel = new SkillLevel(id, name);
    skillLevel.setStatus(ESkillLevelStatus.NOT_STARTED);
    skillLevel.setAmses(List.of());
    skillLevel.setTraces(List.of());

    return skillLevel;
  }

  public static SkillLevel toDomain(
      UUID id,
      String name,
      ESkillLevelStatus status,
      List<Trace> traces,
      List<AMS> amses,
      Skill skill) {
    var skillLevel = new SkillLevel(id, name);
    skillLevel.setStatus(status);
    skillLevel.setTraces(traces);
    skillLevel.setAmses(amses);
    skillLevel.setSkill(skill);

    return skillLevel;
  }

  @Override
  public String toString() {
    return "SkillLevel[%s]".formatted(id);
  }
}
