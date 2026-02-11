package fr.avenirsesr.portfolio.activity.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "activity",
    indexes = {@Index(name = "idx_activity_thematic", columnList = "idx_activity_thematic")})
@NoArgsConstructor
@Getter
@Setter
public class ActivityEntity extends AvenirsBaseEntity {

  @Column(nullable = false, length = TITLE_LENGTH)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EActivityThematic thematic;

  @Column(nullable = false, length = DESCRIPTION_LENGTH)
  private String summary;

  @Column(name = "execution_period_info", nullable = false, length = ACTIVITY_EXECUTION_PERIOD_INFO)
  private String executionPeriodInfo;

  private ActivityEntity(
      UUID id,
      String title,
      EActivityThematic thematic,
      String summary,
      String executionPeriodInfo,
      Instant createdAt,
      Instant updatedAt) {

    this.setId(id);
    this.title = title;
    this.thematic = thematic;
    this.summary = summary;
    this.executionPeriodInfo = executionPeriodInfo;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static ActivityEntity of(
      UUID id,
      String title,
      EActivityThematic thematic,
      String summary,
      String executionPeriodInfo,
      Instant createdAt,
      Instant updatedAt) {

    return new ActivityEntity(
        id, title, thematic, summary, executionPeriodInfo, createdAt, updatedAt);
  }
}
