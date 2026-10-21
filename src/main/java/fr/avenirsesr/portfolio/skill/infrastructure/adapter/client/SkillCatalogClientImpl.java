package fr.avenirsesr.portfolio.skill.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillCatalogDTO;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.skill.domain.port.output.client.SkillCatalogClient;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class SkillCatalogClientImpl implements SkillCatalogClient {

  private final WebClient webClient;

  @Value("${avenirs.interoperability.api-key}")
  private String apiKey;

  @Value("${avenirs.interoperability.skill-catalog.endpoint}")
  private String skillCatalogEndpoint;

  public SkillCatalogClientImpl(@Qualifier("skillCatalogWebClient") WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  public Optional<SkillCatalogDTO> fetchNationalCatalog(ESkillReferentialType type) {
    try {
      log.info("Fetching the {} skill catalog from interoperability", type);
      return Optional.ofNullable(
          webClient
              .get()
              .uri(skillCatalogEndpoint + "/" + type.name())
              .header(AvenirsSecurityHeaders.API_KEY, apiKey)
              .retrieve()
              .bodyToMono(SkillCatalogDTO.class)
              .block());
    } catch (Exception e) {
      log.error(
          "Failed to fetch the {} skill catalog from interoperability at '{}'. Error: {}",
          type,
          skillCatalogEndpoint,
          e.getMessage());
      log.debug("Full error details:", e);
      return Optional.empty();
    }
  }
}
