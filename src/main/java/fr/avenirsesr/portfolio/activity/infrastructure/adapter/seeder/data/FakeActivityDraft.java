package fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityDraftEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import java.time.Instant;
import java.util.UUID;
import net.datafaker.Faker;

public class FakeActivityDraft {
  private static final Faker faker = new Faker();
  private final ActivityDraftEntity activityDraft;

  private FakeActivityDraft(ActivityDraftEntity activityDraft) {
    this.activityDraft = activityDraft;
  }

  public static FakeActivityDraft create(StaffEntity author) {
    return new FakeActivityDraft(
        ActivityDraftEntity.of(
            UUID.randomUUID(),
            faker.job().position(),
            author,
            faker.options().option(EActivityThematic.values()),
            faker.lorem().paragraph(2),
            faker.lorem().paragraph(2),
            faker.lorem().sentence(15),
            faker.lorem().sentence(4),
            faker.number().numberBetween(0, 10),
            faker.number().numberBetween(0, 10),
            faker.bool().bool(),
            null,
            Instant.now(),
            Instant.now()));
  }

  public ActivityDraftEntity toEntity() {
    return activityDraft;
  }
}
