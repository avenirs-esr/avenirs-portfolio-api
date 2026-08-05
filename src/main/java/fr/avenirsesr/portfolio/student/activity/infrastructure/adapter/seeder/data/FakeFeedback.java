package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.AssociationsJson;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.FeedbackEntity;
import java.util.List;
import java.util.UUID;
import net.datafaker.Faker;

public class FakeFeedback {
  private static final Faker faker = new Faker();
  private final FeedbackEntity feedback;

  private FakeFeedback(FeedbackEntity feedback) {
    this.feedback = feedback;
  }

  public static FakeFeedback of(DeclaredActivityEntity declaredActivity) {
    FeedbackEntity entity =
        new FeedbackEntity(
            declaredActivity,
            faker.lorem().paragraph(3),
            null,
            EFeedbackStatus.NEW,
            1,
            new AssociationsJson(List.of(), List.of()));
    entity.setId(UUID.fromString(faker.internet().uuid()));
    return new FakeFeedback(entity);
  }

  public FeedbackEntity toEntity() {
    return feedback;
  }
}
