package fr.avenirsesr.portfolio.student.progress.domain.model;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillLevelProgress extends AvenirsBaseModel {
  private final Student student;
  private final SkillLevel skillLevel;
  private ESkillLevelStatus status;
  private LocalDate startDate;
  private LocalDate endDate;
  private List<Trace> traces;
  private List<AMS> amses;

  private SkillLevelProgress(
      UUID id,
      Student student,
      SkillLevel skillLevel,
      ESkillLevelStatus status,
      LocalDate startDate,
      LocalDate endDate,
      List<Trace> traces,
      List<AMS> amses) {
    super(id);
    this.student = student;
    this.skillLevel = skillLevel;
    this.status = status;
    this.startDate = startDate;
    this.endDate = endDate;
    this.traces = traces;
    this.amses = amses;
  }

  public static SkillLevelProgress create(
      UUID id, Student student, SkillLevel skillLevel, LocalDate startDate, LocalDate endDate) {
    return new SkillLevelProgress(
        id,
        student,
        skillLevel,
        ESkillLevelStatus.NOT_STARTED,
        startDate,
        endDate,
        List.of(),
        List.of());
  }

  public static SkillLevelProgress toDomain(
      UUID id,
      Student student,
      SkillLevel skillLevel,
      ESkillLevelStatus status,
      LocalDate startDate,
      LocalDate endDate,
      List<Trace> traces,
      List<AMS> amses) {
    return new SkillLevelProgress(
        id, student, skillLevel, status, startDate, endDate, traces, amses);
  }

  public static Comparator<SkillLevelProgress> comparatorOf(SortCriteria sortCriteria) {
    Comparator<SkillLevelProgress> comparator =
        switch (sortCriteria.field()) {
          case NAME -> Comparator.comparing(e -> e.getSkillLevel().getSkill().getName());
          case DATE -> Comparator.comparing(SkillLevelProgress::getStartDate);
        };

    comparator =
        switch (sortCriteria.order()) {
          case ASC -> comparator;
          case DESC -> comparator.reversed();
        };

    return comparator;
  }
}
