package fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import java.time.Instant;
import java.util.UUID;
import net.datafaker.Faker;

public class FakeActivity {
  private static final Faker faker = new Faker();
  private final ActivityEntity activity;

  private FakeActivity(ActivityEntity activity) {
    this.activity = activity;
  }

  public static FakeActivity create() {
    return new FakeActivity(
        ActivityEntity.of(
            UUID.randomUUID(),
            faker.job().position(),
            faker.options().option(EActivityThematic.values()),
            faker.lorem().paragraph(2),
            faker.lorem().sentence(15),
            Instant.now(),
            Instant.now()));
  }

  public ActivityEntity toEntity() {
    return activity;
  }
}
