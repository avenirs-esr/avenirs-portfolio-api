package fr.avenirsesr.portfolio.activity.infrastructure.fixture;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import java.time.Instant;
import java.util.UUID;

public class ActivityFixture {

  private UUID id = UUID.randomUUID();
  private String title = "Default Title";
  private EActivityThematic thematic = EActivityThematic.EXPERIENCES;
  private String summary = "Default Summary";
  private String executionPeriodInfo = "Default Execution Period Info";
  private String executionPeriodInfoSummary = "Short Execution Period Info";
  private Instant createdAt = Instant.now();
  private Instant updatedAt = Instant.now();

  public static ActivityFixture create() {
    return new ActivityFixture();
  }

  public ActivityFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public ActivityFixture withTitle(String title) {
    this.title = title;
    return this;
  }

  public ActivityFixture withThematic(EActivityThematic thematic) {
    this.thematic = thematic;
    return this;
  }

  public ActivityFixture withSummary(String summary) {
    this.summary = summary;
    return this;
  }

  public ActivityFixture withExecutionPeriodInfo(String executionPeriodInfo) {
    this.executionPeriodInfo = executionPeriodInfo;
    return this;
  }

  public ActivityFixture withExecutionPeriodInfoSummary(String executionPeriodInfoSummary) {
    this.executionPeriodInfoSummary = executionPeriodInfoSummary;
    return this;
  }

  public ActivityFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public ActivityFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public Activity toModel() {
    return Activity.toDomain(
        id,
        title,
        thematic,
        summary,
        executionPeriodInfo,
        executionPeriodInfoSummary,
        createdAt,
        updatedAt);
  }
}
