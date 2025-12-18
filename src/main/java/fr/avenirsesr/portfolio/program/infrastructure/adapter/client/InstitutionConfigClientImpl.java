package fr.avenirsesr.portfolio.program.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.configuration.domain.model.InstitutionConfigurationElements;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.program.domain.port.output.client.InstitutionConfigClient;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class InstitutionConfigClientImpl implements InstitutionConfigClient {
  private final WebClient webClient;

  @Value("${avenirs.back-office.api-key}")
  private String apiKey;

  @Value("${avenirs.back-office.institution.config.endpoint}")
  private String institutionConfigBackOfficeEndPoint;

  public InstitutionConfigClientImpl(WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  public InstitutionConfigurationElements getInstitutionConfigElementsById(UUID institutionId) {
    try {
      log.debug(
          "Fetching institution configuration from back-office: {}",
          institutionConfigBackOfficeEndPoint);
      return webClient
          .get()
          .uri(institutionConfigBackOfficeEndPoint + "/" + institutionId)
          .header(AvenirsSecurityHeaders.API_KEY, apiKey)
          .retrieve()
          .bodyToMono(InstitutionConfigurationElements.class)
          .block();
    } catch (Exception e) {
      log.error(
          "Failed to fetch institution configuration from back-office at '{}'. Error: {}",
          institutionConfigBackOfficeEndPoint,
          e.getMessage());
      log.debug("Full error details:", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "Unable to fetch institution configuration from back-office",
          e);
    }
  }
}
