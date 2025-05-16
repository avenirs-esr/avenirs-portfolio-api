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

  private SkillLevel(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  public static SkillLevel create(String name) {
    var skillLevel = new SkillLevel(UUID.randomUUID(), name);
    skillLevel.setStatus(ESkillLevelStatus.NOT_STARTED);
    skillLevel.setAmses(List.of());
    skillLevel.setTraces(List.of());

    return skillLevel;
  }

  public static SkillLevel toDomain(
      UUID id, String name, ESkillLevelStatus status, List<Trace> traces, List<AMS> amses) {
    var skillLevel = new SkillLevel(id, name);
    skillLevel.setStatus(status);
    skillLevel.setTraces(traces);
    skillLevel.setAmses(amses);

    return skillLevel;
  }
}
