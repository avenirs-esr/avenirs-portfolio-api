package fr.avenirsesr.portfolio.program.infrastructure.fixture;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkillLevelProgressFixture {
  private final UUID id;
  private final Student student;
  private final SkillLevel skillLevel;
  private ESkillLevelStatus status;
  private LocalDate startDate;
  private LocalDate endDate;
  private List<Trace> traces;
  private List<AMS> amses;

  private SkillLevelProgressFixture(Student student, SkillLevel skillLevel) {
    this.id = UUID.randomUUID();
    this.student = student;
    this.skillLevel = skillLevel;
    this.status = ESkillLevelStatus.NOT_STARTED;
    this.startDate = LocalDate.now().minusMonths(2);
    this.endDate = LocalDate.now().plusMonths(2);
    this.traces = List.of();
    this.amses = List.of();
  }

  public static SkillLevelProgressFixture create(Student student, SkillLevel skillLevel) {
    return new SkillLevelProgressFixture(student, skillLevel);
  }

  public static SkillLevelProgressFixture create(Student student) {
    return new SkillLevelProgressFixture(student, SkillLevelFixture.create().toModel());
  }

  public static List<SkillLevelProgressFixture> createMany(Student student, int nb) {
    var res = new ArrayList<SkillLevelProgressFixture>();
    for (int i = 0; i < nb; i++) {
      res.add(create(student));
    }
    return res;
  }

  public SkillLevelProgressFixture withStartDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  public SkillLevelProgressFixture withEndDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }

  public SkillLevelProgressFixture withTraces(List<Trace> traces) {
    this.traces = traces;
    return this;
  }

  public SkillLevelProgressFixture withAmses(List<AMS> amses) {
    this.amses = amses;
    return this;
  }

  public SkillLevelProgressFixture withStatus(ESkillLevelStatus status) {
    LocalDate pastStartDate = LocalDate.now().minus(Period.ofYears(2));
    LocalDate pastEndDate = LocalDate.now().minus(Period.ofYears(1));
    LocalDate futureStartDate = LocalDate.now().plus(Period.ofYears(1));
    LocalDate futureEndDate = LocalDate.now().plus(Period.ofYears(2));
    this.status = status;
    switch (status) {
      case VALIDATED, FAILED -> {
        this.startDate = pastStartDate;
        this.endDate = pastEndDate;
      }
      case UNDER_ACQUISITION, UNDER_REVIEW -> {
        this.startDate = pastStartDate;
        this.endDate = futureEndDate;
      }
      case TO_BE_EVALUATED, NOT_STARTED -> {
        this.startDate = futureStartDate;
        this.endDate = futureEndDate;
      }
    }
    return this;
  }

  public SkillLevelProgress toModel() {
    return SkillLevelProgress.toDomain(
        id, student, skillLevel, status, startDate, endDate, traces, amses);
  }
}
