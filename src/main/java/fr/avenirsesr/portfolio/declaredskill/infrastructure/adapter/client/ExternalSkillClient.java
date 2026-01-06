package fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
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

  @Cacheable(value = "externalSkillById", key = "#externalSkillId")
  public Optional<ExternalSkillDTO> getById(UUID externalSkillId) {
    try {
      log.debug("Fetching external skill from interoperability: {}", externalSkillId);
      ExternalSkillDetailsDTO details =
          webClient
              .get()
              .uri(externalSkillEndpoint + "/" + externalSkillId)
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
          externalSkillId,
          externalSkillEndpoint,
          e.getMessage());
      log.debug("Full error details:", e);
      return Optional.empty();
    }
  }

  @Cacheable(value = "externalSkillDetails", key = "#externalSkillId")
  public Optional<ExternalSkillDetailsDTO> getExternalSkillDetails(UUID externalSkillId) {
    try {
      log.debug("Fetching external skill details from interoperability: {}", externalSkillId);
      ExternalSkillDetailsDTO result =
          webClient
              .get()
              .uri(externalSkillEndpoint + "/" + externalSkillId)
              .header(AvenirsSecurityHeaders.API_KEY, apiKey)
              .retrieve()
              .bodyToMono(ExternalSkillDetailsDTO.class)
              .block();
      return Optional.ofNullable(result);
    } catch (Exception e) {
      log.error(
          "Failed to fetch external skill details {} from interoperability at '{}'. Error: {}",
          externalSkillId,
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

  @Cacheable(
      value = "external-skill-search",
      key = "#keyword + '-' + #pageCriteria.page() + '-' + #pageCriteria.pageSize()")
  public PagedResponse<ExternalSkillDTO> search(String keyword, PageCriteria pageCriteria) {
    try {
      log.debug("Searching external skills from interoperability: {}", keyword);

      String uri =
          String.format(
              "%s/search?keyword=%s&page=%d&pageSize=%d",
              externalSkillEndpoint, keyword, pageCriteria.page(), pageCriteria.pageSize());

      PagedResponse<ExternalSkillDTO> result =
          webClient
              .get()
              .uri(uri)
              .header(AvenirsSecurityHeaders.API_KEY, apiKey)
              .retrieve()
              .bodyToMono(new ParameterizedTypeReference<PagedResponse<ExternalSkillDTO>>() {})
              .block();

      return result != null ? result : emptyPagedResponse(pageCriteria);
    } catch (Exception e) {
      log.error(
          "Failed to search external skills from interoperability at '{}'. Error: {}",
          externalSkillEndpoint,
          e.getMessage());
      log.debug("Full error details:", e);
      return emptyPagedResponse(pageCriteria);
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

  private PagedResponse<ExternalSkillDTO> emptyPagedResponse(PageCriteria pageCriteria) {
    return new PagedResponse<>(
        List.of(), new PageInfoDTO(pageCriteria.page(), pageCriteria.pageSize(), 0L, 0));
  }
}
