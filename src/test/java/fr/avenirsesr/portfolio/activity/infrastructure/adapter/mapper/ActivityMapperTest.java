package fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StaffMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeStaff;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivityMapperTest {

  private Activity activity;
  private final UUID id = UUID.randomUUID();
  private final String title = "Test Activity";
  private final Staff author =
      StaffMapper.INSTANCE.toDomain(
          FakeStaff.create(UserMapper.INSTANCE.fromDomain(UserFixture.create().toModel()))
              .toEntity());
  private final EActivityThematic thematic = EActivityThematic.SELF_KNOWLEDGE;
  private final String summary = "Activity summary";
  private final String description = "<h3>Objectives</h3><p>Test activity description</p>";
  private final String executionPeriodInfo = "2020 - 2022";
  private final String executionPeriodInfoSummary = "label 2020";
  private final int traceAllowedAssociations = 10;
  private final int feedbackAllowedIterations = 10;
  private final boolean enableRefection = true;
  private final File banner =
      File.create(
          UUID.randomUUID(),
          EFileType.PNG,
          "activity banner",
          1000L,
          1,
          "exemple.com/image.png",
          author.getUser(),
          true);
  private final Instant createdAt = Instant.parse("2023-01-01T00:00:00Z");
  private final Instant updatedAt = Instant.parse("2023-12-31T23:59:59Z");
  private final List<String> links =
      List.of("https://example.com/link1", "https://example.com/link2");

  @BeforeEach
  void setUp() {
    activity =
        Activity.toDomain(
            id,
            author,
            title,
            thematic,
            summary,
            EActivityStatus.PUBLISHED,
            description,
            executionPeriodInfo,
            executionPeriodInfoSummary,
            enableRefection,
            traceAllowedAssociations,
            feedbackAllowedIterations,
            banner,
            links,
            List.of(),
            createdAt,
            updatedAt);
  }

  @Test
  void shouldMapFromDomainToEntity() {
    BddLogger.given("an Activity mapper");

    BddLogger.when("mapping a domain Activity to ActivityEntity");
    ActivityEntity entity = ActivityMapper.INSTANCE.fromDomain(activity);

    BddLogger.then("it should return a correct ActivityEntity");
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(author.getId(), entity.getAuthor().getId());
    assertEquals(title, entity.getTitle());
    assertEquals(thematic, entity.getThematic());
    assertEquals(summary, entity.getSummary());
    assertEquals(description, entity.getDescription());
    assertEquals(executionPeriodInfo, entity.getExecutionPeriodInfo());
    assertEquals(executionPeriodInfoSummary, entity.getExecutionPeriodInfoSummary());
    assertEquals(traceAllowedAssociations, entity.getTraceAllowedAssociations());
    assertEquals(feedbackAllowedIterations, entity.getFeedbackAllowedIterations());
    assertEquals(enableRefection, entity.isEnableReflection());
    assertEquals(createdAt, entity.getCreatedAt());
    assertEquals(updatedAt, entity.getUpdatedAt());
  }

  @Test
  void shouldMapFromEntityToDomain() {
    BddLogger.given("an Activity mapper");

    ActivityEntity entity = new ActivityEntity();
    entity.setId(id);
    entity.setAuthor(StaffMapper.INSTANCE.fromDomain(author));
    entity.setTitle(title);
    entity.setThematic(thematic);
    entity.setSummary(summary);
    entity.setDescription(description);
    entity.setExecutionPeriodInfo(executionPeriodInfo);
    entity.setExecutionPeriodInfoSummary(executionPeriodInfoSummary);
    entity.setTraceAllowedAssociations(traceAllowedAssociations);
    entity.setFeedbackAllowedIterations(feedbackAllowedIterations);
    entity.setEnableReflection(enableRefection);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);

    BddLogger.when("mapping an ActivityEntity to domain Activity");
    Activity mappedActivity = ActivityMapper.INSTANCE.toDomain(entity);

    BddLogger.then("it should return a correct domain Activity");
    assertNotNull(mappedActivity);
    assertEquals(id, mappedActivity.getId());
    assertEquals(author, mappedActivity.getAuthor());
    assertEquals(title, mappedActivity.getTitle());
    assertEquals(thematic, mappedActivity.getThematic());
    assertEquals(summary, mappedActivity.getSummary());
    assertEquals(description, mappedActivity.getDescription());
    assertEquals(executionPeriodInfo, mappedActivity.getExecutionPeriodInfo().get());
    assertEquals(executionPeriodInfoSummary, mappedActivity.getExecutionPeriodInfoSummary().get());
    assertEquals(traceAllowedAssociations, mappedActivity.getTraceAllowedAssociations());
    assertEquals(feedbackAllowedIterations, mappedActivity.getFeedbackAllowedIterations());
    assertEquals(enableRefection, mappedActivity.isEnableReflection());
    assertEquals(createdAt, mappedActivity.getCreatedAt());
    assertEquals(updatedAt, mappedActivity.getUpdatedAt());
  }
}
