package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillCategoryDTO;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDTO;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDetailsDTO;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class ExternalSkillClient {

  private final WebClient webClient;

  @Value("${avenirs.interoperability.api-key}")
  private String apiKey;

  @Value("${avenirs.interoperability.external-skill.endpoint}")
  private String externalSkillEndpoint;

  @Value("${avenirs.interoperability.actuator.health}")
  private String healthEndpoint;

  public ExternalSkillClient(WebClient webClient) {
    this.webClient = webClient;
  }

  @Cacheable(value = "externalSkillById", key = "#id")
  public Optional<ExternalSkillDTO> getById(UUID id) {
    try {
      log.debug("Fetching external skill from interoperability: {}", id);
      ExternalSkillDetailsDTO details =
          webClient
              .get()
              .uri(externalSkillEndpoint + "/" + id)
              .header(AvenirsSecurityHeaders.API_KEY, apiKey)
              .retrieve()
              .bodyToMono(ExternalSkillDetailsDTO.class)
              .block();

      if (details == null) {
        return Optional.empty();
      }

      ExternalSkillDTO result =
          new ExternalSkillDTO(
              details.id(),
              details.title(),
              details.categoryPath().stream().map(ExternalSkillCategoryDTO::libelle).toList(),
              details.type());

      return Optional.of(result);
    } catch (Exception e) {
      log.error(
          "Failed to fetch external skill {} from interoperability at '{}'. Error: {}",
          id,
          externalSkillEndpoint,
          e.getMessage());
      log.debug("Full error details:", e);
      return Optional.empty();
    }
  }

  @Cacheable(value = "externalSkillDetails", key = "#id")
  public Optional<ExternalSkillDetailsDTO> getExternalSkillDetails(UUID id) {
    try {
      log.debug("Fetching external skill details from interoperability: {}", id);
      ExternalSkillDetailsDTO result =
          webClient
              .get()
              .uri(externalSkillEndpoint + "/" + id)
              .header(AvenirsSecurityHeaders.API_KEY, apiKey)
              .retrieve()
              .bodyToMono(ExternalSkillDetailsDTO.class)
              .block();
      return Optional.ofNullable(result);
    } catch (Exception e) {
      log.error(
          "Failed to fetch external skill details {} from interoperability at '{}'. Error: {}",
          id,
          externalSkillEndpoint,
          e.getMessage());
      log.debug("Full error details:", e);
      return Optional.empty();
    }
  }

  public List<ExternalSkillDTO> getRandomSkills(int count) {
    try {
      log.debug("Fetching {} random external skills from interoperability", count);
      List<ExternalSkillDTO> result =
          webClient
              .get()
              .uri(externalSkillEndpoint + "/random?count=" + count)
              .header(AvenirsSecurityHeaders.API_KEY, apiKey)
              .retrieve()
              .bodyToMono(new ParameterizedTypeReference<List<ExternalSkillDTO>>() {})
              .block();
      return result != null ? result : List.of();
    } catch (Exception e) {
      log.error(
          "Failed to fetch random external skills from interoperability at '{}'. Error: {}",
          externalSkillEndpoint,
          e.getMessage());
      log.debug("Full error details:", e);
      return List.of();
    }
  }

  public boolean checkInteroperabilityMicroservice() {
    try {
      log.debug("Checking interoperability microservice health at: {}", healthEndpoint);
      String response =
          webClient.get().uri(healthEndpoint).retrieve().bodyToMono(String.class).block();

      log.info("Interoperability microservice is UP");
      return response != null && response.contains("\"status\":\"UP\"");
    } catch (Exception e) {
      log.error(
          "Interoperability microservice health check failed at '{}'. Error: {}",
          healthEndpoint,
          e.getMessage());
      log.debug("Full error details:", e);
      return false;
    }
  }
}
