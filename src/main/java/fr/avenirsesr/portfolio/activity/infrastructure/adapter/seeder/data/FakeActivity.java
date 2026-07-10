package fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import net.datafaker.Faker;

public class FakeActivity {
  private static final Faker faker = new Faker();
  private final ActivityEntity activity;

  private FakeActivity(ActivityEntity activity) {
    this.activity = activity;
  }

  public static FakeActivity create(StaffEntity author) {
    return new FakeActivity(
        ActivityEntity.of(
            UUID.randomUUID(),
            author,
            faker.job().position(),
            faker.options().option(EActivityThematic.values()),
            faker.lorem().paragraph(2),
            EActivityStatus.PUBLISHED,
            faker.lorem().paragraph(2),
            faker.lorem().sentence(15),
            faker.lorem().sentence(4),
            faker.number().numberBetween(-1, 10),
            faker.number().numberBetween(-1, 10),
            faker.bool().bool(),
            null,
            List.of(),
            List.of(),
            Instant.now(),
            Instant.now()));
  }

  public ActivityEntity toEntity() {
    return activity;
  }
}
