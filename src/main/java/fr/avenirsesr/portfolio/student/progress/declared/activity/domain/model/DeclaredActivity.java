package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeclaredActivity extends AvenirsBaseModel {
  private final Student student;
  private final Activity activity;
  private boolean hasStarted;
  private String reflection;
  private LocalDate startDate;
  private LocalDate endDate;
  private Instant finishedAt;

  private DeclaredActivity(
      UUID id,
      Student student,
      Activity activity,
      boolean hasStarted,
      String reflection,
      LocalDate startDate,
      LocalDate endDate,
      Instant finishedAt,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.student = student;
    this.activity = activity;
    this.hasStarted = hasStarted;
    this.reflection = reflection;
    this.startDate = startDate;
    this.endDate = endDate;
    this.finishedAt = finishedAt;
  }

  public EDeclaredActivityStatus getStatus() {
    if (!hasStarted) {
      return EDeclaredActivityStatus.SUBSCRIBED;
    }
    return EDeclaredActivityStatus.IN_PROGRESS;
  }

  public static DeclaredActivity create(
      Student student,
      Activity activity,
      boolean hasStarted,
      String reflection,
      LocalDate startDate,
      LocalDate endDate,
      Instant finishedAt) {
    return new DeclaredActivity(
        UUID.randomUUID(),
        student,
        activity,
        hasStarted,
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
      boolean hasStarted,
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
        hasStarted,
        reflection,
        startDate,
        endDate,
        finishedAt,
        createdAt,
        updatedAt);
  }
}
