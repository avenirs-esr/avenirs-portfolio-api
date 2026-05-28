package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.seeder.domain.model.enums.ESeedMode;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.configuration.SeedingState;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeederRunner implements CommandLineRunner {

  private final UserRepository userRepository;
  private final UserPrincipalRepository userPrincipalRepository;
  private final SeederOrchestrator seederOrchestrator;
  private final SeedingState seedingState;

  @Value("${seeder.enabled:false}")
  private boolean seedEnabled;

  @Override
  public void run(String... args) {
    if (!seedEnabled) {
      log.info("Seeder disabled: skipped");
      seedingState.markCompleted();
      return;
    }

    long userCount = userRepository.countAll();
    if (userCount > 0) {
      log.info("{} users found. Seeder skipped.", userCount);
      long userPrincipalCount = userPrincipalRepository.countAll();
      if (userPrincipalCount < userCount) {
        seederOrchestrator.seedTable("user-principal", ESeedMode.INSERT_ONLY);
        seedingState.markCompleted();
        return;
      }
      log.info("{} user principal found. Seeder skipped.", userPrincipalCount);
      seedingState.markCompleted();
      return;
    }

    seederOrchestrator.seedAll();
  }
}
