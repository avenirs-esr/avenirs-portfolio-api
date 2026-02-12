package fr.avenirsesr.portfolio.student.progress.imported.domain.model;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.enums.EActivityProgressStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityProgress extends AvenirsBaseModel {
  private final Student student;
  private final Activity activity;
  private EActivityProgressStatus status;
  private String reflection;
    private LocalDate startDate;
    private LocalDate endDate;

  private ActivityProgress(
      UUID id,
      Student student,
      Activity activity,
      EActivityProgressStatus status,
      String reflection,
      LocalDate startDate,
      LocalDate endDate,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.student = student;
    this.activity = activity;
      this.status = status;
      this.reflection = reflection;
      this.startDate = startDate;
      this.endDate = endDate;
  }

  public static ActivityProgress create(
      Student student, Activity activity, EActivityProgressStatus status,
      String reflection, LocalDate startDate, LocalDate endDate) {
    return new ActivityProgress(
            UUID.randomUUID(),
        student,
        activity,
            status,
            reflection,
        startDate,
        endDate,
        Instant.now(),
        Instant.now());
  }

  public static ActivityProgress toDomain(
      UUID id,
      Student student,
      Activity activity,
      EActivityProgressStatus status,
      String reflection,
      LocalDate startDate,
      LocalDate endDate,
      Instant createdAt,
      Instant updatedAt) {
    return new ActivityProgress(
        id, student, activity, status,
            reflection, startDate, endDate, createdAt, updatedAt);
  }
}
