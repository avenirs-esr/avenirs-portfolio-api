package fr.avenirsesr.portfolio.activity.infrastructure.fixture;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StaffMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeStaff;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.UUID;

public class ActivityFixture {

  private UUID id = UUID.randomUUID();
  private Staff author =
      StaffMapper.INSTANCE.toDomain(
          FakeStaff.create(UserMapper.INSTANCE.fromDomain(UserFixture.create().toModel()))
              .toEntity());
  private String title = "Default Title";
  private EActivityThematic thematic = EActivityThematic.EXPERIENCES;
  private String summary = "Default Summary";
  private String description = "<h3>Objectives</h3><p>Default Description</p>";
  private String executionPeriodInfo = "Default Execution Period Info";
  private String executionPeriodInfoSummary = "Short Execution Period Info";
  private int traceAllowedAssociations = 10;
  private int feedbackAllowedIterations = 10;
  private boolean enableRefection = true;
  private Instant createdAt = Instant.now();
  private Instant updatedAt = Instant.now();

  public static ActivityFixture create() {
    return new ActivityFixture();
  }

  public ActivityFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public ActivityFixture withAuthor(Staff author) {
    this.author = author;
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

  public ActivityFixture withDescription(String description) {
    this.description = description;
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

  public ActivityFixture withTraceAllowedAssociations(int traceAllowedAssociations) {
    this.traceAllowedAssociations = traceAllowedAssociations;
    return this;
  }

  public ActivityFixture withFeedbackAllowedIterations(Integer feedbackAllowedIterations) {
    this.feedbackAllowedIterations = feedbackAllowedIterations;
    return this;
  }

  public ActivityFixture withEnableRefection(boolean enableRefection) {
    this.enableRefection = enableRefection;
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
        author,
        title,
        thematic,
        summary,
        description,
        executionPeriodInfo,
        executionPeriodInfoSummary,
        enableRefection,
        traceAllowedAssociations,
        feedbackAllowedIterations,
        createdAt,
        updatedAt);
  }
}
