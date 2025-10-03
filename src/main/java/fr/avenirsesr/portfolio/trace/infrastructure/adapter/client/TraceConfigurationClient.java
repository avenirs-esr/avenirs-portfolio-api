package fr.avenirsesr.portfolio.trace.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.trace.domain.port.output.TraceConfigurationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class TraceConfigurationClient implements TraceConfigurationPort {

  private final WebClient webClient;

  @Value("${avenirs.back-office.trace.config.endpoint}")
  private String traceConfigBackOfficeEndPoint;

  public TraceConfigurationClient(WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  public TraceConfiguration getTraceConfiguration() {
    try {
      log.debug("Fetching trace configuration from back-office: {}", traceConfigBackOfficeEndPoint);
      return webClient
          .get()
          .uri(traceConfigBackOfficeEndPoint)
          .retrieve()
          .bodyToMono(TraceConfiguration.class)
          .block();
    } catch (Exception e) {
      log.error(
          "Failed to fetch trace configuration from back-office at '{}'. Error: {}",
          traceConfigBackOfficeEndPoint,
          e.getMessage());
      log.debug("Full error details:", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "Unable to fetch trace configuration from back-office",
          e);
    }
  }
}
