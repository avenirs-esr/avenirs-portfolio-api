package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillSyncService;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.client.ExternalSkillClient;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdditionalSkillSeeder {
  private final ExternalSkillClient externalSkillClient;
  private final AdditionalSkillSyncService additionalSkillSyncService;

  @Value("${seeder.source:FAKER}")
  private String seederSource;

  public boolean checkInteroperabilityMicroserviceIfNeeded() {
    return "FAKER".equalsIgnoreCase(seederSource)
        || externalSkillClient.checkInteroperabilityMicroservice();
  }

  @Transactional
  public List<AdditionalSkillEntity> seed() {
    log.info("Seeding additional skills from interoperability...");

    var externalSkills = externalSkillClient.getRandomSkills(200);
    log.info("Retrieved {} random external skills", externalSkills.size());

    var additionalSkillEntities =
        externalSkills.stream()
            .map(
                externalSkill ->
                    additionalSkillSyncService
                        .getOrCreateFromExternalSkill(externalSkill.id())
                        .map(AdditionalSkillMapper::fromDomain)
                        .orElse(null))
            .filter(entity -> entity != null)
            .toList();

    log.info("✔ {} additionalSkill synced", additionalSkillEntities.size());

    return additionalSkillEntities;
  }
}
