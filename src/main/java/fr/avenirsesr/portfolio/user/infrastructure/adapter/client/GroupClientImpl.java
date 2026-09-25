package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.group.application.adapter.dto.GroupDTO;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.user.domain.port.output.client.GroupClient;
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
public class GroupClientImpl implements GroupClient {

  private final WebClient webClient;

  @Value("${avenirs.back-office.api-key}")
  private String apiKey;

  @Value("${avenirs.back-office.group.endpoint}")
  private String groupEndpoint;

  public GroupClientImpl(WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  @Cacheable(value = "groupById", key = "#id", unless = "#result == null")
  public Optional<GroupDTO> getById(UUID id) {
    return fetch(groupEndpoint + "/" + id, "group " + id);
  }

  @Override
  @Cacheable(value = "programByGroupId", key = "#groupId", unless = "#result == null")
  public Optional<GroupDTO> getProgramOfGroup(UUID groupId) {
    return fetch(groupEndpoint + "/" + groupId + "/program", "program of group " + groupId);
  }

  private Optional<GroupDTO> fetch(String uri, String what) {
    try {
      log.debug("Fetching {} from back-office", what);
      return Optional.ofNullable(
          webClient
              .get()
              .uri(uri)
              .header(AvenirsSecurityHeaders.API_KEY, apiKey)
              .retrieve()
              .bodyToMono(GroupDTO.class)
              .block());
    } catch (WebClientResponseException.NotFound e) {
      log.debug("Back-office does not know the {}", what);
      return Optional.empty();
    } catch (Exception e) {
      log.error(
          "Failed to fetch {} from back-office at '{}'. Error: {}", what, uri, e.getMessage());
      log.debug("Full error details:", e);
      return Optional.empty();
    }
  }
}
