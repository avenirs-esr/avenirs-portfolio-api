package fr.avenirsesr.portfolio.program.infrastructure.fixture;

import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkillLevelProgressFixture {
  private final UUID id;
  private final Student student;
  private SkillLevel skillLevel;
  private ESkillLevelStatus status;
  private LocalDate startDate;
  private LocalDate endDate;
  private static final Period DEFAULT_SKILL_LEVEL_PERIOD = Period.ofMonths(3);
  private Instant createdAt;
  private Instant updatedAt;

  private SkillLevelProgressFixture(Student student, SkillLevel skillLevel) {
    this.id = UUID.randomUUID();
    this.student = student;
    this.skillLevel = skillLevel;
    this.status = ESkillLevelStatus.NOT_STARTED;
    this.startDate = LocalDate.now().minusMonths(2);
    this.endDate = startDate.plus(DEFAULT_SKILL_LEVEL_PERIOD);
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
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
    this.endDate = startDate.plus(DEFAULT_SKILL_LEVEL_PERIOD);
    return this;
  }

  public SkillLevelProgressFixture withStartDate(LocalDate startDate, Period skillLevelPeriod) {
    this.startDate = startDate;
    this.endDate = startDate.plus(skillLevelPeriod);
    return this;
  }

  public SkillLevelProgressFixture withEndDate(LocalDate endDate) {
    this.startDate = endDate.minus(DEFAULT_SKILL_LEVEL_PERIOD);
    this.endDate = endDate;
    return this;
  }

  public SkillLevelProgressFixture withEndDate(LocalDate endDate, Period skillLevelPeriod) {
    this.startDate = endDate.minus(skillLevelPeriod);
    this.endDate = endDate;
    return this;
  }

  public SkillLevelProgressFixture withSkillLevel(SkillLevel skillLevel) {
    this.skillLevel = skillLevel;
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

  public SkillLevelProgressFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public SkillLevelProgressFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public SkillLevelProgress toModel() {
    return SkillLevelProgress.toDomain(
        id, student, skillLevel, status, startDate, endDate, createdAt, updatedAt);
  }
}
