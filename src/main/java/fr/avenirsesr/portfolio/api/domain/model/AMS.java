package fr.avenirsesr.portfolio.api.domain.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.EAmsStatus;
import java.time.Instant;
import java.util.List;
import java.util.Set;
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

  @Setter(AccessLevel.NONE)
  private final String title;

  @Setter(AccessLevel.NONE)
  private final Instant startDate;

  @Setter(AccessLevel.NONE)
  private final Instant endDate;

  private EAmsStatus status;

  private List<SkillLevel> skillLevels;

  private List<Trace> traces;

  private Set<Cohort> cohorts;

  private AMS(UUID id, User user, String title, Instant startDate, Instant endDate) {
    this.id = id;
    this.user = user;
    this.title = title;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public static AMS create(UUID id, User user, String title, Instant startDate, Instant endDate) {
    var ams = new AMS(id, user, title, startDate, endDate);
    ams.setSkillLevels(List.of());
    ams.setTraces(List.of());
    ams.setCohorts(Set.of());
    ams.setStatus(EAmsStatus.NOT_STARTED);

    return ams;
  }

  public static AMS toDomain(
      UUID id,
      User user,
      String title,
      Instant startDate,
      Instant endDate,
      List<SkillLevel> skillLevels,
      List<Trace> traces,
      Set<Cohort> cohorts,
      EAmsStatus status) {
    var ams = new AMS(id, user, title, startDate, endDate);
    ams.setSkillLevels(skillLevels);
    ams.setTraces(traces);
    ams.setCohorts(cohorts);
    ams.setStatus(status);
    return ams;
  }
}
