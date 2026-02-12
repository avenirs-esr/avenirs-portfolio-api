package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.common.temporal.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.enums.EActivityProgressStatus;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.REFLECTION_LENGTH;

@Entity
@Table(
    name = "activity_progress",
    indexes = {
      @Index(name = "idx_ap_student", columnList = "student_id"),
      @Index(name = "idx_ap_student_activity", columnList = "student_id, activity_id")
    })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ActivityProgressEntity extends PeriodEntity<LocalDate> {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private StudentEntity student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "activity_id")
  private ActivityEntity activity;

    @Column
    @Enumerated(EnumType.STRING)
    private EActivityProgressStatus status;

    @Size(max = REFLECTION_LENGTH, message = "reflection can not exceed {max} characters")
    private String reflection;

  private ActivityProgressEntity(
      UUID id,
      StudentEntity student,
      ActivityEntity activity,
      EActivityProgressStatus status,
      String reflection,
      LocalDate startDate,
      LocalDate endDate) {
    setId(id);
    this.student = student;
    this.activity = activity;
    this.status = status;
    this.reflection = reflection;
      this.startDate = startDate;
      this.endDate = endDate;
  }

  public static ActivityProgressEntity of(
      UUID id,
      StudentEntity student,
      ActivityEntity activity,
      EActivityProgressStatus status,
      String reflection,
      LocalDate startDate,
      LocalDate endDate) {
    return new ActivityProgressEntity(
        id, student, activity, status, reflection, startDate, endDate);
  }

  @Override
  public String toString() {
    return "ActivityProgressEntity[%s]".formatted(getId());
  }
}
