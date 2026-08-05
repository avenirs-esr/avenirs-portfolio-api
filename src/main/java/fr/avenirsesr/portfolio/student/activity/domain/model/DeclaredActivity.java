package fr.avenirsesr.portfolio.student.activity.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeclaredActivity extends AvenirsBaseModel {
  private final Student student;
  private final Activity activity;

  @Getter(AccessLevel.NONE)
  private Instant startedAt;

  private String reflection;
  private LocalDate startDate;
  private LocalDate endDate;

  @Getter(AccessLevel.NONE)
  private Instant finishedAt;

  private boolean valorized;

  private DeclaredActivity(
      UUID id,
      Student student,
      Activity activity,
      Instant startedAt,
      String reflection,
      LocalDate startDate,
      LocalDate endDate,
      Instant finishedAt,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.student = student;
    this.activity = activity;
    this.startedAt = startedAt;
    this.reflection = reflection;
    this.startDate = startDate;
    this.endDate = endDate;
    this.finishedAt = finishedAt;
    this.valorized = valorized;
  }

  public static DeclaredActivity create(
      UUID id,
      Student student,
      Activity activity,
      Instant startedAt,
      String reflection,
      LocalDate startDate,
      LocalDate endDate,
      Instant finishedAt) {
    return new DeclaredActivity(
        id,
        student,
        activity,
        startedAt,
        reflection,
        startDate,
        endDate,
        finishedAt,
        false,
        Instant.now(),
        Instant.now());
  }

  public static DeclaredActivity toDomain(
      UUID id,
      Student student,
      Activity activity,
      Instant startedAt,
      String reflection,
      LocalDate startDate,
      LocalDate endDate,
      Instant finishedAt,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt) {
    return new DeclaredActivity(
        id,
        student,
        activity,
        startedAt,
        reflection,
        startDate,
        endDate,
        finishedAt,
        valorized,
        createdAt,
        updatedAt);
  }

  public Optional<Instant> getFinishedAt() {
    return Optional.ofNullable(finishedAt);
  }

  public Optional<Instant> getStartedAt() {
    return Optional.ofNullable(startedAt);
  }
}
