package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.institution.application.adapter.dto.InstitutionDTO;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.user.domain.port.output.client.InstitutionClient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component
public class InstitutionClientImpl implements InstitutionClient {

  private final WebClient webClient;

  @Value("${avenirs.back-office.api-key}")
  private String apiKey;

  @Value("${avenirs.back-office.institution.endpoint}")
  private String institutionEndpoint;

  public InstitutionClientImpl(WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  @Cacheable(value = "institutionById", key = "#id", unless = "#result == null")
  public Optional<InstitutionDTO> getById(UUID id) {
    try {
      log.debug("Fetching institution {} from back-office", id);
      return Optional.ofNullable(
          webClient
              .get()
              .uri(institutionEndpoint + "/" + id)
              .header(AvenirsSecurityHeaders.API_KEY, apiKey)
              .retrieve()
              .bodyToMono(InstitutionDTO.class)
              .block());
    } catch (WebClientResponseException.NotFound e) {
      log.debug("Back-office does not know the institution {}", id);
      return Optional.empty();
    } catch (Exception e) {
      log.error(
          "Failed to fetch institution {} from back-office at '{}'. Error: {}",
          id,
          institutionEndpoint,
          e.getMessage());
      log.debug("Full error details:", e);
      return Optional.empty();
    }
  }

  @Override
  public boolean hasAccess(List<UUID> affiliatedIds, List<UUID> targetIds) {
    try {
      log.debug(
          "Checking access to institutions {} for affiliations {} against back-office",
          targetIds,
          affiliatedIds);
      return webClient
          .post()
          .uri(institutionEndpoint + "/staff/access-check")
          .header(AvenirsSecurityHeaders.API_KEY, apiKey)
          .bodyValue(new InstitutionAccessCheckRequest(affiliatedIds, targetIds))
          .retrieve()
          .bodyToMono(Boolean.class)
          .defaultIfEmpty(false)
          .block();
    } catch (Exception e) {
      log.error(
          "Failed to check institution access at '{}'. Error: {}",
          institutionEndpoint,
          e.getMessage());
      log.debug("Full error details:", e);
      return false;
    }
  }

  private record InstitutionAccessCheckRequest(
      List<UUID> affiliatedInstitutionIds, List<UUID> targetInstitutionIds) {}
}
