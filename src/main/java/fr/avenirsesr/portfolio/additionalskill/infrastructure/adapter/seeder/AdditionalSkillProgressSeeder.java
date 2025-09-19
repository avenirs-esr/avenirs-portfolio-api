package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder;

import static fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.SeederConfig.MAX_ADDITIONAL_SKILLS_PER_STUDENT;
import static fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.SeederConfig.MIN_ADDITIONAL_SKILLS_PER_STUDENT;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository.AdditionalSkillDatabaseProgressRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.fake.FakeAdditionalSkillProgress;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdditionalSkillProgressSeeder {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(AdditionalSkillSeeder.class, SharedDataGenerator.class);

  private final AdditionalSkillDatabaseProgressRepository studentAdditionalSkillDatabaseRepository;

  @Transactional
  public List<AdditionalSkillProgressEntity> seed(
      List<UserEntity> savedStudents, List<AdditionalSkillEntity> savedAdditionalSkills) {
    log.info("Seeding student progress...");
    List<AdditionalSkillProgressEntity> studentAdditionalSkillEntities = new ArrayList<>();
    savedStudents.forEach(
        student -> {
          int additionalSkillsCount =
              dataGenerator
                  .with("ADDITIONAL_SKILLS_PER_STUDENT")
                  .number(MIN_ADDITIONAL_SKILLS_PER_STUDENT, MAX_ADDITIONAL_SKILLS_PER_STUDENT);
          List<UUID> bannedSkillsIds = new ArrayList<>();
          for (int i = 0; i < additionalSkillsCount; i++) {
            AdditionalSkillProgressEntity fakeStudentAdditionalSkillProgress =
                FakeAdditionalSkillProgress.of(student, savedAdditionalSkills, bannedSkillsIds)
                    .toEntity();
            bannedSkillsIds.add(fakeStudentAdditionalSkillProgress.getAdditionalSkillId());
            studentAdditionalSkillEntities.add(fakeStudentAdditionalSkillProgress);
          }
        });
    studentAdditionalSkillDatabaseRepository.saveAllEntities(studentAdditionalSkillEntities);
    log.info("✔ {} studentAdditionalSkills created", studentAdditionalSkillEntities.size());
    return studentAdditionalSkillEntities;
  }
}
