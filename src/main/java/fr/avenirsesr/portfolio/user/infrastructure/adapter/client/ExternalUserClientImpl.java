package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.user.application.adapter.dto.ExternalUserDTO;
import fr.avenirsesr.portfolio.user.domain.port.output.client.ExternalUserClient;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class ExternalUserClientImpl implements ExternalUserClient {

  private final WebClient webClient;

  @Value("${avenirs.interoperability.api-key}")
  private String apiKey;

  @Value("${avenirs.interoperability.external-user.get-by-eppn}")
  private String getByEppnEndpoint;

  public ExternalUserClientImpl(WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  public Optional<ExternalUserDTO> getByEppn(String eppn) {
    try {
      ExternalUserDTO result =
          webClient
              .get()
              .uri(getByEppnEndpoint + "/" + eppn)
              .header(AvenirsSecurityHeaders.API_KEY, apiKey)
              .retrieve()
              .bodyToMono(ExternalUserDTO.class)
              .block();

      return Optional.ofNullable(result);
    } catch (Exception e) {
      log.error("Failed to fetch external user by eppn {}", eppn, e);
      return Optional.empty();
    }
  }

  @Override
  public void activateByEppn(String eppn) {
    try {
      log.debug("Activating external user in interoperability for eppn {}", eppn);

      webClient
          .patch()
          .uri(getByEppnEndpoint + "/" + eppn + "/activate")
          .header(AvenirsSecurityHeaders.API_KEY, apiKey)
          .retrieve()
          .toBodilessEntity()
          .block();

    } catch (Exception e) {
      log.error(
          "Failed to activate external user {} in interoperability. Error: {}",
          eppn,
          e.getMessage());
      log.debug("Full error details:", e);
      throw e;
    }
  }
}
