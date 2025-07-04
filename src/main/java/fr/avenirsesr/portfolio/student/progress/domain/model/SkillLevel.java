package fr.avenirsesr.portfolio.student.progress.domain.model;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.student.progress.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  private final String description;

  private ESkillLevelStatus status;
  private List<Trace> traces;
  private List<AMS> amses;
  private Skill skill;
  private LocalDate startDate;
  private LocalDate endDate;

  private SkillLevel(UUID id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public static SkillLevel create(UUID id, String name, String description) {
    var skillLevel = new SkillLevel(id, name, description);
    skillLevel.setStatus(ESkillLevelStatus.NOT_STARTED);
    skillLevel.setAmses(List.of());
    skillLevel.setTraces(List.of());

    return skillLevel;
  }

  public static SkillLevel toDomain(
      UUID id,
      String name,
      String description,
      ESkillLevelStatus status,
      List<Trace> traces,
      List<AMS> amses,
      Skill skill,
      LocalDate startDate,
      LocalDate endDate) {
    var skillLevel = new SkillLevel(id, name, description);
    skillLevel.setStatus(status);
    skillLevel.setTraces(traces);
    skillLevel.setAmses(amses);
    skillLevel.setSkill(skill);
    skillLevel.setStartDate(startDate);
    skillLevel.setEndDate(endDate);

    return skillLevel;
  }

  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  @Override
  public String toString() {
    return "SkillLevel[%s]".formatted(id);
  }
}
