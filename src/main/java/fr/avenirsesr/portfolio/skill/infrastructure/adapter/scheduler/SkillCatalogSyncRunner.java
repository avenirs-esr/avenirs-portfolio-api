package fr.avenirsesr.portfolio.skill.infrastructure.adapter.scheduler;

import fr.avenirsesr.portfolio.skill.domain.model.SkillReferential;
import fr.avenirsesr.portfolio.skill.domain.port.input.SkillSyncService;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillReferentialRepository;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@Order(0)
@RequiredArgsConstructor
public class SkillCatalogSyncRunner implements CommandLineRunner {
  private final SkillSyncService skillSyncService;
  private final SkillRepository skillRepository;
  private final SkillReferentialRepository skillReferentialRepository;

  @Value("${skill.catalog.sync.on-startup:true}")
  private boolean syncOnStartup;

  @Override
  public void run(String... args) {
    if (!syncOnStartup) {
      log.info("National skill catalog synchronization on startup disabled: skipped");
      return;
    }

    if (!isCatalogEmpty()) {
      log.info("National skill catalog already populated, synchronization on startup skipped");
      return;
    }

    log.info("National skill catalog is empty, synchronizing from interoperability");
    skillSyncService.syncNationalCatalogs();
  }

  private boolean isCatalogEmpty() {
    return skillReferentialRepository.findAllNational().stream().mapToLong(this::countSkills).sum()
        == 0;
  }

  private long countSkills(SkillReferential referential) {
    return skillRepository.countByReferential(referential);
  }
}
