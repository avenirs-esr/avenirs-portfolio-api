package fr.avenirsesr.portfolio.ams.domain.model;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AMS extends AvenirsBaseModel {
  private final User user;
  private final String title;
  private final Instant startDate;
  private final Instant endDate;

  private EAmsStatus status;
  private List<SkillLevelProgress> skillLevels;
  private List<Trace> traces;
  private Set<Cohort> cohorts;

  private AMS(UUID id, User user, String title, Instant startDate, Instant endDate) {
    super(id);
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
      List<SkillLevelProgress> skillLevels,
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
