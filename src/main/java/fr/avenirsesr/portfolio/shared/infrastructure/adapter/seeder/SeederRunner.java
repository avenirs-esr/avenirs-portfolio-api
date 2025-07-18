package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.AMSSeeder;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.CohortSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.InstitutionSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.ProgramSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.SkillSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.TrainingPathSeeder;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.*;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.TraceSeeder;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SeederRunner implements CommandLineRunner {
  private final UserRepository userRepository;
  private final UserSeeder userSeeder;
  private final CohortSeeder cohortSeeder;
  private final AMSSeeder amsSeeder;
  private final TraceSeeder traceSeeder;
  private final InstitutionSeeder institutionSeeder;
  private final ProgramSeeder programSeeder;
  private final TrainingPathSeeder trainingPathSeeder;
  private final StudentProgressSeeder studentProgressSeeder;
  private final SkillSeeder skillSeeder;

  @Value("${seeder.enabled:false}")
  private boolean seedEnabled;

  public SeederRunner(
      UserRepository userRepository,
      UserSeeder userSeeder,
      CohortSeeder cohortSeeder,
      AMSSeeder amsSeeder,
      TraceSeeder traceSeeder,
      InstitutionSeeder institutionSeeder,
      ProgramSeeder programSeeder,
      TrainingPathSeeder trainingPathSeeder,
      StudentProgressSeeder studentProgressSeeder,
      SkillSeeder skillSeeder) {
    this.userRepository = userRepository;
    this.cohortSeeder = cohortSeeder;
    this.amsSeeder = amsSeeder;
    this.traceSeeder = traceSeeder;
    this.userSeeder = userSeeder;
    this.institutionSeeder = institutionSeeder;
    this.programSeeder = programSeeder;
    this.trainingPathSeeder = trainingPathSeeder;
    this.studentProgressSeeder = studentProgressSeeder;
    this.skillSeeder = skillSeeder;
  }

  @Override
  public void run(String... args) {
    long userCont = userRepository.countAll();

    if (seedEnabled && userCont == 0) {
      log.info("Seeding enabled and starting...");

      var savedUsers = userSeeder.seed();
      var savedInstitutions = institutionSeeder.seed();
      var savedPrograms = programSeeder.seed(savedInstitutions);
      var savedTraces = traceSeeder.seed(savedUsers);
      var savedSkillLevels = skillSeeder.seed(savedPrograms);
      var savedSkills =
          savedSkillLevels.stream().map(SkillLevelEntity::getSkill).distinct().toList();
      var savedStudents = savedUsers.stream().filter(u -> u.getStudent().isPresent()).toList();
      var savedTrainingPaths = trainingPathSeeder.seed(savedPrograms, savedSkillLevels);
      var savedStudentProgresses =
          studentProgressSeeder.seed(savedTrainingPaths, savedStudents, savedSkillLevels);
      var savedSkillLevelProgresses =
          savedStudentProgresses.stream().flatMap(s -> s.getSkillLevels().stream()).toList();
      var savedCohorts = cohortSeeder.seed(savedUsers, savedTrainingPaths);
      var savedAmses =
          amsSeeder.seed(savedUsers, savedSkillLevelProgresses, savedTraces, savedCohorts);

      log.info("✔ Seeding successfully finished");
    } else log.info("{} users found. Seeder is disabled: seeding skipped", userCont);
  }
}
