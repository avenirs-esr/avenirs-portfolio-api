package fr.avenirsesr.portfolio.skill.infrastructure.adapter.scheduler;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.skill.domain.port.input.SkillSyncService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class SkillCatalogSyncScheduler {
  private final SkillSyncService skillSyncService;

  @Scheduled(cron = "${skill.catalog.sync.cron}")
  public void synchronize() {
    log.info("Starting the scheduled national skill catalog synchronization");
    try {
      List<ESkillReferentialType> failed = skillSyncService.syncNationalCatalogs();
      if (!failed.isEmpty()) {
        log.warn("National skill catalogs not synchronized: {}", failed);
      }
    } catch (Exception e) {
      log.error("Scheduled national skill catalog synchronization failed: {}", e.getMessage());
      log.debug("Full error details:", e);
    }
  }
}
