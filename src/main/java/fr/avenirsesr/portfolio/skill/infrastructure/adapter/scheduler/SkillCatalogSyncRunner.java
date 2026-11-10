package fr.avenirsesr.portfolio.skill.infrastructure.adapter.scheduler;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.skill.domain.port.input.SkillSyncService;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

  @Value("${skill.catalog.sync.retry.max-attempts:20}")
  private int maxAttempts;

  @Value("${skill.catalog.sync.retry.delay:30s}")
  private Duration retryDelay;

  private final ScheduledExecutorService retryExecutor =
      Executors.newSingleThreadScheduledExecutor(
          runnable -> {
            Thread thread = new Thread(runnable, "skill-catalog-sync-retry");
            thread.setDaemon(true);
            return thread;
          });

  @Override
  public void run(String... args) {
    if (!syncOnStartup) {
      log.info("Skill catalog synchronization on startup is disabled");
      return;
    }

    synchronize(1);
  }

  private void synchronize(int attempt) {
    log.info("Synchronizing the national skill catalogs from interoperability");
    List<ESkillReferentialType> pending = List.of();
    try {
      pending = skillSyncService.syncNationalCatalogs();
    } catch (Exception e) {
      log.error("National skill catalog synchronization on startup failed: {}", e.getMessage());
      log.debug("Full error details:", e);
    }

    if (!pending.isEmpty() && attempt < maxAttempts) {
      log.warn("National skill catalogs {} not synchronized, retrying in {}", pending, retryDelay);
      retryExecutor.schedule(
          () -> synchronize(attempt + 1), retryDelay.toMillis(), TimeUnit.MILLISECONDS);
    }
  }
}
