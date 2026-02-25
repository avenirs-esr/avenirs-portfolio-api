package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import net.datafaker.Faker;

public class FakeDeclaredActivity {
  private static final Faker faker = new Faker();
  private final DeclaredActivityEntity declaredActivity;

  private FakeDeclaredActivity(DeclaredActivityEntity declaredActivity) {
    this.declaredActivity = declaredActivity;
  }

  public static FakeDeclaredActivity create(StudentEntity student, ActivityEntity activity) {
    var hasStarted = faker.bool().bool();
    var hasDate = faker.bool().bool();
    var hasFinished = faker.bool().bool();
    return new FakeDeclaredActivity(
        DeclaredActivityEntity.of(
            UUID.fromString(faker.internet().uuid()),
            student,
            activity,
            hasStarted ? Instant.now() : null,
            hasStarted ? faker.lorem().paragraph(3) : null,
            hasDate ? LocalDate.now().minusMonths(2) : null,
            hasDate ? LocalDate.now().minusMonths(1) : null,
            hasFinished ? Instant.now() : null));
  }

  public DeclaredActivityEntity toEntity() {
    return declaredActivity;
  }
}
