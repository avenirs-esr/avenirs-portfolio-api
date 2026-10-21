package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.seeder;

import static fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig.MAX_SKILL_PROGRESSES_PER_STUDENT;
import static fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig.MIN_SKILL_PROGRESSES_PER_STUDENT;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.model.SkillProgressEntity;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.repository.SkillProgressDatabaseRepository;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.seeder.fake.FakeSkillProgress;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
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
public class SkillProgressSeeder {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(SkillProgressSeeder.class, SharedDataGenerator.class);

  private final SkillProgressDatabaseRepository skillProgressDatabaseRepository;

  @Transactional
  public List<SkillProgressEntity> seed(
      List<StudentEntity> savedStudents, List<Skill> availableSkills) {
    log.info("Seeding student progress...");
    List<SkillProgressEntity> skillProgressEntities = new ArrayList<>();
    savedStudents.forEach(
        student -> {
          int skillProgressesCount =
              dataGenerator
                  .with("DECLARED_SKILLS_PER_STUDENT")
                  .number(MIN_SKILL_PROGRESSES_PER_STUDENT, MAX_SKILL_PROGRESSES_PER_STUDENT);
          List<UUID> bannedSkillsIds = new ArrayList<>();
          for (int i = 0; i < skillProgressesCount; i++) {
            SkillProgressEntity fakeStudentSkillProgress =
                FakeSkillProgress.of(student, availableSkills, bannedSkillsIds).toEntity();
            bannedSkillsIds.add(fakeStudentSkillProgress.getSkill().getId());
            skillProgressEntities.add(fakeStudentSkillProgress);
          }
        });
    skillProgressDatabaseRepository.saveAllEntities(skillProgressEntities);
    log.info("✔ {} skill progresses created", skillProgressEntities.size());
    return skillProgressEntities;
  }
}
