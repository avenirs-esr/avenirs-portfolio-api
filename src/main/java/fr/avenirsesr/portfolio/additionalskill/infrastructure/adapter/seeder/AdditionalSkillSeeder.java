package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder;

import static fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig.MAX_ADDITIONAL_SKILLS_PER_STUDENT;
import static fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig.MIN_ADDITIONAL_SKILLS_PER_STUDENT;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository.AdditionalSkillDatabaseProgressRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.fake.FakeAdditionalSkillProgress;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdditionalSkillSeeder {
  private static final FakerProvider faker = new FakerProvider();

  private final AdditionalSkillDatabaseProgressRepository studentAdditionalSkillDatabaseRepository;

  public List<AdditionalSkillProgressEntity> seed(List<UserEntity> savedStudents) {
    log.info("Seeding student progress...");
    List<AdditionalSkillProgressEntity> studentAdditionalSkillEntities = new ArrayList<>();
    savedStudents.forEach(
        student -> {
          int additionalSkillsCount =
              faker
                  .call()
                  .random()
                  .nextInt(MIN_ADDITIONAL_SKILLS_PER_STUDENT, MAX_ADDITIONAL_SKILLS_PER_STUDENT);
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
