package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.cgu.application.adapter.dto.CguDTO;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.user.domain.port.output.client.CguClient;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component
public class CguClientImpl implements CguClient {

  private final WebClient webClient;

  @Value("${avenirs.back-office.api-key}")
  private String apiKey;

  @Value("${avenirs.back-office.cgu.latest.endpoint}")
  private String latestCguEndpoint;

  public CguClientImpl(WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  public Optional<CguDTO> getLatest() {
    try {
      return Optional.ofNullable(
          webClient
              .get()
              .uri(latestCguEndpoint)
              .header(AvenirsSecurityHeaders.API_KEY, apiKey)
              .retrieve()
              .bodyToMono(CguDTO.class)
              .block());
    } catch (WebClientResponseException.NotFound e) {
      log.warn("No terms of use published in the back-office yet");
      return Optional.empty();
    }
  }
}
