package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository.AdditionalSkillDatabaseProgressRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.fake.FakeAdditionalSkillProgress;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentAdditionalSkillSeeder {

  private static final Integer MAX_ADDITIONAL_SKILLS_PER_STUDENT = 4;

  private final AdditionalSkillDatabaseProgressRepository studentAdditionalSkillDatabaseRepository;

  public List<AdditionalSkillProgressEntity> seed(List<UserEntity> savedStudents) {
    log.info("Seeding student progress...");
    List<AdditionalSkillProgressEntity> studentAdditionalSkillEntities = new ArrayList<>();
    savedStudents.forEach(
        student -> {
          int additionalSkillsCount =
              ThreadLocalRandom.current().nextInt(1, MAX_ADDITIONAL_SKILLS_PER_STUDENT + 1);
          List<UUID> bannedSkillsIds = new ArrayList<>();
          for (int i = 0; i < additionalSkillsCount; i++) {
            AdditionalSkillProgressEntity fakeStudentAdditionalSkill =
                FakeAdditionalSkillProgress.of(student, bannedSkillsIds).toEntity();
            bannedSkillsIds.add(fakeStudentAdditionalSkill.getAdditionalSkillId());
            studentAdditionalSkillEntities.add(fakeStudentAdditionalSkill);
          }
        });
    studentAdditionalSkillDatabaseRepository.saveAllEntities(studentAdditionalSkillEntities);
    log.info("✔ {} studentAdditionalSkills created", studentAdditionalSkillEntities.size());
    return studentAdditionalSkillEntities;
  }
}
