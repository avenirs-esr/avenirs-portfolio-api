package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.user.domain.port.output.client.AccessClient;
import fr.avenirsesr.portfolio.user.domain.port.output.client.StudentAccessScope;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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

  private record StaffAccessCheckRequest(
      String eppn, List<UUID> targetInstitutionIds, List<UUID> targetGroupIds) {}

  private record StudentScopeResponse(List<UUID> institutionIds, List<UUID> groupIds) {}
}
