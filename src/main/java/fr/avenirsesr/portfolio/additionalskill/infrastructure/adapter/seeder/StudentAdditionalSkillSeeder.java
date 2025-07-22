package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.StudentAdditionalSkillEntity;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository.StudentAdditionalSkillDatabaseRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.fake.FakeStudentAdditionalSkill;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentAdditionalSkillSeeder {

  private final StudentAdditionalSkillDatabaseRepository studentAdditionalSkillDatabaseRepository;

  public List<StudentAdditionalSkillEntity> seed(List<UserEntity> savedStudents) {
    log.info("Seeding student progress...");
    List<StudentAdditionalSkillEntity> studentAdditionalSkillEntities = new ArrayList<>();
    savedStudents.forEach(
        student -> {
          studentAdditionalSkillEntities.add(
              FakeStudentAdditionalSkill.of(student, "104174").toEntity());
          studentAdditionalSkillEntities.add(
              FakeStudentAdditionalSkill.of(student, "104175").toEntity());
        });
    studentAdditionalSkillDatabaseRepository.saveAllEntities(studentAdditionalSkillEntities);
    log.info("✔ {} studentAdditionalSkills created", studentAdditionalSkillEntities.size());
    return studentAdditionalSkillEntities;
  }
}
