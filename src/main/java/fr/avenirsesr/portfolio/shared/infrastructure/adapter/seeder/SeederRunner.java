package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.AMSSeeder;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.CohortSeeder;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.InstitutionSeeder;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.ProgramProgressSeeder;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.ProgramSeeder;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.SkillSeeder;
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
  private final ProgramProgressSeeder programProgressSeeder;
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
      ProgramProgressSeeder programProgressSeeder,
      SkillSeeder skillSeeder) {
    this.userRepository = userRepository;
    this.cohortSeeder = cohortSeeder;
    this.amsSeeder = amsSeeder;
    this.traceSeeder = traceSeeder;
    this.userSeeder = userSeeder;
    this.institutionSeeder = institutionSeeder;
    this.programSeeder = programSeeder;
    this.programProgressSeeder = programProgressSeeder;
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
      var savedSkills = skillSeeder.seed(savedPrograms);
      var savedProgramProgresses =
          programProgressSeeder.seed(savedPrograms, savedUsers, savedSkills);
      var savedCohorts = cohortSeeder.seed(savedUsers, savedProgramProgresses);
      var savedSkillLevels =
          savedSkills.stream().flatMap(s -> s.getSkillLevels().stream()).toList();
      var savedAmses = amsSeeder.seed(savedUsers, savedSkillLevels, savedTraces, savedCohorts);

      log.info("✔ Seeding successfully finished");
    } else log.info("{} users found. Seeder is disabled: seeding skipped", userCont);
  }
}
