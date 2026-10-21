package fr.avenirsesr.portfolio.skill.infrastructure.adapter.scheduler;

import fr.avenirsesr.portfolio.skill.domain.port.input.SkillSyncService;
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

  @Value("${skill.catalog.sync.on-startup:true}")
  private boolean syncOnStartup;

  @Override
  public void run(String... args) {
    if (!syncOnStartup) {
      log.info("National skill catalog synchronization on startup disabled: skipped");
      return;
    }

    try {
      log.info("Synchronizing the national skill catalogs from interoperability");
      skillSyncService.syncNationalCatalogs();
    } catch (Exception e) {
      log.error("National skill catalog synchronization on startup failed: {}", e.getMessage());
      log.debug("Full error details:", e);
    }
  }
}
