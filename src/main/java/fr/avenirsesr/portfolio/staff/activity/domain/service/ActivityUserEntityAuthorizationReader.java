package fr.avenirsesr.portfolio.staff.activity.domain.service;

import fr.avenirsesr.portfolio.authorization.domain.data.UserEntityAuthorizationsData;
import fr.avenirsesr.portfolio.authorization.domain.port.output.UserEntityAuthorizationReader;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorization;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorizationEntityType;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorizationSummary;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityAuthorizationRepository;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ActivityUserEntityAuthorizationReader implements UserEntityAuthorizationReader {
  private final ActivityAuthorizationRepository activityAuthorizationRepository;

  @Override
  public EAuthorizationEntityType entityType() {
    return EAuthorizationEntityType.ACTIVITY;
  }

  @Override
  public boolean hasAny(UUID staffId, Set<UUID> institutionIds) {
    return activityAuthorizationRepository.existsByStaffIdAndTargetInstitutionIds(
        staffId, institutionIds);
  }

  @Override
  public PagedResult<UserEntityAuthorizationsData> findPage(
      UUID staffId, Set<UUID> institutionIds, PageCriteria pageCriteria) {
    PagedResult<ActivityAuthorizationSummary> page =
        activityAuthorizationRepository.findPageByStaffIdAndTargetInstitutionIds(
            staffId, institutionIds, pageCriteria);
    return new PagedResult<>(
        page.content().stream()
            .map(
                summary ->
                    new UserEntityAuthorizationsData(
                        summary.activityId(),
                        summary.activityTitle(),
                        summary.rights().stream()
                            .map(right -> EAuthorization.valueOf(right.name()))
                            .collect(Collectors.toSet())))
            .toList(),
        page.pageInfo());
  }
}
