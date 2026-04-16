package fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivityMapperTest {

  private Activity activity;
  private final UUID id = UUID.randomUUID();
  private final String title = "Test Activity";
  private final EActivityThematic thematic = EActivityThematic.SELF_KNOWLEDGE;
  private final String summary = "Activity summary";
  private final String description = "<h3>Objectives</h3><p>Test activity description</p>";
  private final String executionPeriodInfo = "2020 - 2022";
  private final String executionPeriodInfoSummary = "label 2020";
  private final Instant createdAt = Instant.parse("2023-01-01T00:00:00Z");
  private final Instant updatedAt = Instant.parse("2023-12-31T23:59:59Z");

  @BeforeEach
  void setUp() {
    activity =
        Activity.toDomain(
            id,
            title,
            thematic,
            summary,
            description,
            executionPeriodInfo,
            executionPeriodInfoSummary,
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
    assertEquals(title, entity.getTitle());
    assertEquals(thematic, entity.getThematic());
    assertEquals(summary, entity.getSummary());
    assertEquals(description, entity.getDescription());
    assertEquals(executionPeriodInfo, entity.getExecutionPeriodInfo());
    assertEquals(createdAt, entity.getCreatedAt());
    assertEquals(updatedAt, entity.getUpdatedAt());
  }

  @Test
  void shouldMapFromEntityToDomain() {
    BddLogger.given("an Activity mapper");

    ActivityEntity entity = new ActivityEntity();
    entity.setId(id);
    entity.setTitle(title);
    entity.setThematic(thematic);
    entity.setSummary(summary);
    entity.setDescription(description);
    entity.setExecutionPeriodInfo(executionPeriodInfo);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);

    BddLogger.when("mapping an ActivityEntity to domain Activity");
    Activity mappedActivity = ActivityMapper.INSTANCE.toDomain(entity);

    BddLogger.then("it should return a correct domain Activity");
    assertNotNull(mappedActivity);
    assertEquals(id, mappedActivity.getId());
    assertEquals(title, mappedActivity.getTitle());
    assertEquals(thematic, mappedActivity.getThematic());
    assertEquals(summary, mappedActivity.getSummary());
    assertEquals(description, mappedActivity.getDescription());
    assertEquals(executionPeriodInfo, mappedActivity.getExecutionPeriodInfo());
    assertEquals(createdAt, mappedActivity.getCreatedAt());
    assertEquals(updatedAt, mappedActivity.getUpdatedAt());
  }
}
