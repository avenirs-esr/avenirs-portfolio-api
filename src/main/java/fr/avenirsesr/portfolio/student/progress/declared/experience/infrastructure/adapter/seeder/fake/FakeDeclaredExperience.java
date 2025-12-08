package fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.model.DeclaredExperienceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import net.datafaker.Faker;

public class FakeDeclaredExperience {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeDeclaredExperience.class, SharedDataGenerator.class);
  private final DeclaredExperienceEntity declaredExperience;

  private FakeDeclaredExperience(DeclaredExperienceEntity declaredExperience) {
    this.declaredExperience = declaredExperience;
  }

  private static final Faker faker = new Faker();

  public static FakeDeclaredExperience of(StudentEntity student) {
    return new FakeDeclaredExperience(
        DeclaredExperienceEntity.of(
            dataGenerator.with("id").uuid(),
            Instant.now(),
            Instant.now(),
            student,
            "title - %s".formatted(faker.lorem().sentence(4)),
            dataGenerator.with("EExperienceType").pickIn(EExperienceType.class),
            "organization - %s".formatted(faker.lorem().sentence(2)),
            "activitySector - %s".formatted(faker.lorem().sentence(2)),
            "location - %s".formatted(faker.address().city()),
            "description - %s".formatted(faker.lorem().sentence(10)),
            "sourceOfInformation - %s".formatted(faker.lorem().sentence(5)),
            "summary - %s".formatted(faker.lorem().sentence(10)),
            "externalLink - %s".formatted(faker.internet().url()),
            LocalDate.now().minusWeeks(5),
            null));
  }

  public DeclaredExperienceEntity toEntity() {
    return declaredExperience;
  }

  public List<DeclaredExperienceEntity> generateFor(List<StudentEntity> students) {
    return students.stream()
        .map(FakeDeclaredExperience::of)
        .map(FakeDeclaredExperience::toEntity)
        .toList();
  }
}
