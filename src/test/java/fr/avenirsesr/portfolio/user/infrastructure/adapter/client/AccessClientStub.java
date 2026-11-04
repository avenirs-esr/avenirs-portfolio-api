package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.port.output.client.AccessClient;
import fr.avenirsesr.portfolio.user.domain.port.output.client.StudentAccessScope;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
@Primary
public class AccessClientStub implements AccessClient {

  private final UserPrincipalRepository userPrincipalRepository;
  private final Map<String, StaffAffiliations> staffAffiliationsByEppn = new ConcurrentHashMap<>();
  private final Map<String, StudentAccessScope> studentScopeByEppn = new ConcurrentHashMap<>();

  public AccessClientStub(UserPrincipalRepository userPrincipalRepository) {
    this.userPrincipalRepository = userPrincipalRepository;
  }

  public void setStaffAffiliations(UUID staffId, List<UUID> institutionIds, List<UUID> groupIds) {
    staffAffiliationsByEppn.put(eppnOf(staffId), new StaffAffiliations(institutionIds, groupIds));
  }

  public void setStudentScope(UUID studentId, List<UUID> institutionIds, List<UUID> groupIds) {
    studentScopeByEppn.put(eppnOf(studentId), new StudentAccessScope(institutionIds, groupIds));
  }

  @Override
  public boolean staffHasAccess(
      String eppn, List<UUID> targetInstitutionIds, List<UUID> targetGroupIds) {
    StaffAffiliations affiliations =
        staffAffiliationsByEppn.getOrDefault(eppn, StaffAffiliations.EMPTY);
    boolean institutionsOk =
        isEmpty(targetInstitutionIds)
            || new HashSet<>(affiliations.institutionIds()).containsAll(targetInstitutionIds);
    boolean groupsOk =
        isEmpty(targetGroupIds)
            || new HashSet<>(affiliations.groupIds()).containsAll(targetGroupIds);
    return institutionsOk && groupsOk;
  }

  @Override
  public StudentAccessScope getStudentScope(String eppn) {
    return studentScopeByEppn.getOrDefault(eppn, StudentAccessScope.empty());
  }

  @Override
  public PagedResult<String> searchStaffEppns(
      String eppn, String keyword, PageCriteria pageCriteria) {
    List<String> matching =
        staffAffiliationsByEppn.keySet().stream()
            .filter(
                candidate ->
                    keyword == null || candidate.toLowerCase().contains(keyword.toLowerCase()))
            .sorted()
            .toList();
    int from = Math.min(pageCriteria.page() * pageCriteria.pageSize(), matching.size());
    int to = Math.min(from + pageCriteria.pageSize(), matching.size());
    return new PagedResult<>(
        matching.subList(from, to),
        new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), matching.size()));
  }

  private String eppnOf(UUID userId) {
    return userPrincipalRepository
        .findEppnByUserId(userId)
        .orElseThrow(
            () -> new IllegalStateException("No eppn seeded for user principal " + userId));
  }

  private static boolean isEmpty(List<UUID> ids) {
    return ids == null || ids.isEmpty();
  }

  private record StaffAffiliations(List<UUID> institutionIds, List<UUID> groupIds) {
    static final StaffAffiliations EMPTY = new StaffAffiliations(List.of(), List.of());
  }
}
