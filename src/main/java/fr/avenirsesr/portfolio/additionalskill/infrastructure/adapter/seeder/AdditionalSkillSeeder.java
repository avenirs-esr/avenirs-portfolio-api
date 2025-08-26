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
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdditionalSkillSeeder {
  private static final FakerProvider faker = new FakerProvider().init(AdditionalSkillSeeder.class);

  private final AdditionalSkillDatabaseProgressRepository studentAdditionalSkillDatabaseRepository;

  private final JobLauncher jobLauncher;

  private final Job importROME4CompetenceJob;

  @Transactional
  public List<AdditionalSkillProgressEntity> seed(List<UserEntity> savedStudents) {
    log.info("Seeding additional skills...");
    // seedAdditionalSkills();
    log.info("Seeding student progress...");
    List<AdditionalSkillProgressEntity> studentAdditionalSkillEntities = new ArrayList<>();
    savedStudents.forEach(
        student -> {
          int additionalSkillsCount =
              faker
                  .call("ADDITIONAL_SKILLS_PER_STUDENT")
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

  public void seedAdditionalSkills() {
    JobParameters params =
        new JobParametersBuilder()
            .addLong("time", System.currentTimeMillis()) // identifie l'exécution
            .toJobParameters();

    try {
      jobLauncher.run(importROME4CompetenceJob, params);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
