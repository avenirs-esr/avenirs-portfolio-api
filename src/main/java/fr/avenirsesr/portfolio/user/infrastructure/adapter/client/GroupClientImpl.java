package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.group.application.adapter.dto.GroupDTO;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.user.domain.port.output.client.GroupClient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
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

  @Override
  public boolean hasAccess(List<UUID> affiliatedIds, List<UUID> targetIds) {
    try {
      log.debug(
          "Checking access to groups {} for affiliations {} against back-office",
          targetIds,
          affiliatedIds);
      return webClient
          .post()
          .uri(groupEndpoint + "/staff/access-check")
          .header(AvenirsSecurityHeaders.API_KEY, apiKey)
          .bodyValue(new GroupAccessCheckRequest(affiliatedIds, targetIds))
          .retrieve()
          .bodyToMono(Boolean.class)
          .defaultIfEmpty(false)
          .block();
    } catch (Exception e) {
      log.error("Failed to check group access at '{}'. Error: {}", groupEndpoint, e.getMessage());
      log.debug("Full error details:", e);
      return false;
    }
  }

  @Override
  public List<UUID> getStudentAccessibleIds(List<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    try {
      log.debug("Resolving accessible group ids for affiliations {} from back-office", ids);
      return webClient
          .post()
          .uri(groupEndpoint + "/student/accessible-ids")
          .header(AvenirsSecurityHeaders.API_KEY, apiKey)
          .bodyValue(new GroupAccessibleIdsRequest(ids))
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<List<UUID>>() {})
          .defaultIfEmpty(ids)
          .block();
    } catch (Exception e) {
      log.error(
          "Failed to resolve accessible group ids at '{}'. Error: {}",
          groupEndpoint,
          e.getMessage());
      log.debug("Full error details:", e);
      return ids;
    }
  }

  private record GroupAccessCheckRequest(
      List<UUID> affiliatedGroupIds, List<UUID> targetGroupIds) {}

  private record GroupAccessibleIdsRequest(List<UUID> affiliatedGroupIds) {}
}
