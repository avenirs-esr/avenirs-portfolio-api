package fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.seeder;

import static fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig.MAX_DECLARED_SKILLS_PER_STUDENT;
import static fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig.MIN_DECLARED_SKILLS_PER_STUDENT;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.model.DeclaredSkillEntity;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.seeder.DeclaredSkillSeeder;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.model.DeclaredSkillProgressEntity;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.repository.DeclaredSkillProgressDatabaseRepository;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.seeder.fake.FakeDeclaredSkillProgress;
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
public class DeclaredSkillProgressSeeder {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(DeclaredSkillSeeder.class, SharedDataGenerator.class);

  private final DeclaredSkillProgressDatabaseRepository studentDeclaredSkillDatabaseRepository;

  @Transactional
  public List<DeclaredSkillProgressEntity> seed(
      List<StudentEntity> savedStudents, List<DeclaredSkillEntity> savedDeclaredSkills) {
    log.info("Seeding student progress...");
    List<DeclaredSkillProgressEntity> studentDeclaredSkillEntities = new ArrayList<>();
    savedStudents.forEach(
        student -> {
          int declaredSkillsCount =
              dataGenerator
                  .with("DECLARED_SKILLS_PER_STUDENT")
                  .number(MIN_DECLARED_SKILLS_PER_STUDENT, MAX_DECLARED_SKILLS_PER_STUDENT);
          List<UUID> bannedSkillsIds = new ArrayList<>();
          for (int i = 0; i < declaredSkillsCount; i++) {
            DeclaredSkillProgressEntity fakeStudentDeclaredSkillProgress =
                FakeDeclaredSkillProgress.of(student, savedDeclaredSkills, bannedSkillsIds)
                    .toEntity();
            bannedSkillsIds.add(fakeStudentDeclaredSkillProgress.getDeclaredSkill().getId());
            studentDeclaredSkillEntities.add(fakeStudentDeclaredSkillProgress);
          }
        });
    studentDeclaredSkillDatabaseRepository.saveAllEntities(studentDeclaredSkillEntities);
    log.info("✔ {} studentDeclaredSkills created", studentDeclaredSkillEntities.size());
    return studentDeclaredSkillEntities;
  }
}
