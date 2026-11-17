package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.user.domain.port.output.client.AccessClient;
import fr.avenirsesr.portfolio.user.domain.port.output.client.StudentAccessScope;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class AccessClientImpl implements AccessClient {

  private final WebClient webClient;

  @Value("${avenirs.back-office.api-key}")
  private String apiKey;

  @Value("${avenirs.back-office.access.endpoint}")
  private String accessEndpoint;

  public AccessClientImpl(WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  public boolean staffHasAccess(
      String eppn, List<UUID> targetInstitutionIds, List<UUID> targetGroupIds) {
    try {
      log.debug(
          "Checking staff access for eppn {} to institutions {} / groups {} against back-office",
          eppn,
          targetInstitutionIds,
          targetGroupIds);
      return webClient
          .post()
          .uri(accessEndpoint + "/staff/check")
          .header(AvenirsSecurityHeaders.API_KEY, apiKey)
          .bodyValue(new StaffAccessCheckRequest(eppn, targetInstitutionIds, targetGroupIds))
          .retrieve()
          .bodyToMono(Boolean.class)
          .defaultIfEmpty(false)
          .block();
    } catch (Exception e) {
      log.error(
          "Failed to check staff access for eppn {} at '{}'. Error: {}",
          eppn,
          accessEndpoint,
          e.getMessage());
      log.debug("Full error details:", e);
      return false;
    }
  }

  @Override
  public StudentAccessScope getStudentScope(String eppn) {
    try {
      log.debug("Resolving student scope for eppn {} from back-office", eppn);
      StudentScopeResponse response =
          webClient
              .get()
              .uri(accessEndpoint + "/student/" + eppn + "/scope")
              .header(AvenirsSecurityHeaders.API_KEY, apiKey)
              .retrieve()
              .bodyToMono(StudentScopeResponse.class)
              .block();
      return response == null
          ? StudentAccessScope.empty()
          : new StudentAccessScope(response.institutionIds(), response.groupIds());
    } catch (Exception e) {
      log.error(
          "Failed to resolve student scope for eppn {} at '{}'. Error: {}",
          eppn,
          accessEndpoint,
          e.getMessage());
      log.debug("Full error details:", e);
      return StudentAccessScope.empty();
    }
  }

  @Override
  public PagedResult<String> searchStaffEppns(
      String keyword, List<UUID> institutionIds, List<UUID> groupIds, PageCriteria pageCriteria) {
    try {
      log.debug(
          "Searching staff reachable by (keyword: {},institutionIds{}, groupIds{}) {}"
              + " from back-office",
          keyword,
          institutionIds,
          groupIds,
          pageCriteria);

      UriComponentsBuilder builder =
          UriComponentsBuilder.fromUriString(accessEndpoint + "/staff/search")
              .queryParamIfPresent("keyword", Optional.ofNullable(keyword));

      if (!institutionIds.isEmpty()) {
        builder.queryParam("institutionIds", institutionIds);
      }
      if (!groupIds.isEmpty()) {
        builder.queryParam("groupIds", groupIds);
      }
      URI uri =
          builder
              .queryParam("page", pageCriteria.page())
              .queryParam("pageSize", pageCriteria.pageSize())
              .build()
              .toUri();

      StaffSearchResponse response =
          webClient
              .get()
              .uri(uri)
              .header(AvenirsSecurityHeaders.API_KEY, apiKey)
              .retrieve()
              .bodyToMono(StaffSearchResponse.class)
              .block();
      return response == null
          ? emptySearch(pageCriteria)
          : new PagedResult<>(
              response.data().stream().map(StaffSearchItemResponse::eppn).toList(),
              new PageInfo(
                  response.page().page(),
                  response.page().pageSize(),
                  response.page().totalElements()));
    } catch (Exception e) {
      log.error(
          "Failed to search staff for eppn {} at '{}'. Error: {}", accessEndpoint, e.getMessage());
      log.debug("Full error details:", e);
      return emptySearch(pageCriteria);
    }
  }

  private static PagedResult<String> emptySearch(PageCriteria pageCriteria) {
    return new PagedResult<>(
        List.of(), new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), 0));
  }

  private record StaffSearchResponse(List<StaffSearchItemResponse> data, StaffSearchPage page) {}

  private record StaffSearchPage(int page, int pageSize, long totalElements) {}

  private record StaffSearchItemResponse(String eppn) {}

  private record StaffAccessCheckRequest(
      String eppn, List<UUID> targetInstitutionIds, List<UUID> targetGroupIds) {}

  private record StudentScopeResponse(List<UUID> institutionIds, List<UUID> groupIds) {}
}
