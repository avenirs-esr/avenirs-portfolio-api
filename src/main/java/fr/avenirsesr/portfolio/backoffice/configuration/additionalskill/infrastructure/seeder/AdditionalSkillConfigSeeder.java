package fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.infrastructure.seeder;

import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillLevel;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.port.input.AdditionalSkillConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdditionalSkillConfigSeeder {
  private final AdditionalSkillConfigurationService additionalSkillConfigurationService;

  @Transactional
  public void seed() {
    log.info("Seeding trace configuration...");
    var config =
        new AdditionalSkillConfiguration(
            new AdditionalSkillLevel("Débutant", "pas beaucoup d'experience"),
            new AdditionalSkillLevel("Intermediaire", "un peu d'experience"),
            new AdditionalSkillLevel("Compétent", "bonne connaissance"),
            new AdditionalSkillLevel("Avancé", "pas mal d'experience"),
            new AdditionalSkillLevel("Expert", "parfaite maîtrise"));

    additionalSkillConfigurationService.postConfiguration(config);

    log.info("✔ additional skills configuration saved : {}", config);
  }
}
