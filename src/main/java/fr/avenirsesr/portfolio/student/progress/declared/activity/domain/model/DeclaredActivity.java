package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
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

  private DeclaredActivity(
      UUID id,
      Student student,
      Activity activity,
      Instant startedAt,
      String reflection,
      LocalDate startDate,
      LocalDate endDate,
      Instant finishedAt,
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
  }

  public EDeclaredActivityStatus getStatus() {
    if (finishedAt != null) {
      return EDeclaredActivityStatus.COMPLETED;
    }
    if (startedAt != null) {
      return EDeclaredActivityStatus.IN_PROGRESS;
    }
    return EDeclaredActivityStatus.SUBSCRIBED;
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
