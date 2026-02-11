package fr.avenirsesr.portfolio.activity.domain.model;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Activity extends AvenirsBaseModel {
  private String title;
  private EActivityThematic thematic;
  private String summary;
  private String executionPeriodInfo;

  private Activity(
      UUID id,
      String title,
      EActivityThematic thematic,
      String summary,
      String executionPeriodInfo,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.title = title;
    this.thematic = thematic;
    this.summary = summary;
    this.executionPeriodInfo = executionPeriodInfo;
  }

  public static Activity create(
      UUID id,
      String title,
      EActivityThematic thematic,
      String summary,
      String executionPeriodInfo) {
    Instant now = Instant.now();
    return new Activity(id, title, thematic, summary, executionPeriodInfo, now, now);
  }

  public static Activity toDomain(
      UUID id,
      String title,
      EActivityThematic thematic,
      String summary,
      String executionPeriodInfo,
      Instant createdAt,
      Instant updatedAt) {
    return new Activity(id, title, thematic, summary, executionPeriodInfo, createdAt, updatedAt);
  }
}
