package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** One row per student having consulted an activity, whatever the number of consultations. */
@Entity
@Table(
    name = "activity_view",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_activity_view_activity_student",
          columnNames = {"activity_id", "student_id"})
    },
    indexes = {@Index(name = "idx_activity_view_activity", columnList = "activity_id")})
@NoArgsConstructor
@Getter
@Setter
public class ActivityViewEntity extends AvenirsBaseEntity {
  @Column(name = "activity_id", nullable = false)
  private UUID activityId;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  public static ActivityViewEntity of(UUID activityId, UUID studentId) {
    var entity = new ActivityViewEntity();
    entity.setId(UUID.randomUUID());
    entity.activityId = activityId;
    entity.studentId = studentId;
    return entity;
  }

  @Override
  public String toString() {
    return "ActivityViewEntity[%s]".formatted(getId());
  }
}
