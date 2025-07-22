package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.StudentAdditionalSkillEntity;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.UUID;

public class FakeStudentAdditionalSkill {
  private static final FakerProvider faker = new FakerProvider();
  private final StudentAdditionalSkillEntity studentAdditionalSkillEntity;

  private FakeStudentAdditionalSkill(StudentAdditionalSkillEntity studentAdditionalSkillEntity) {
    this.studentAdditionalSkillEntity = studentAdditionalSkillEntity;
  }

  public static FakeStudentAdditionalSkill of(UserEntity student, String additionalSkillId) {
    return new FakeStudentAdditionalSkill(
        StudentAdditionalSkillEntity.of(
            UUID.fromString(faker.call().internet().uuid()),
            student,
            additionalSkillId,
            faker.call().options().option(EAdditionalSkillType.class),
            faker.call().options().option(ESkillLevelStatus.class)));
  }

  public StudentAdditionalSkillEntity toEntity() {
    return studentAdditionalSkillEntity;
  }
}
