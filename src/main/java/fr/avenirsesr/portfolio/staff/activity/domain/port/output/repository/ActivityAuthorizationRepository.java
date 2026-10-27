package fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorizationSummary;
import java.util.Collection;
import java.util.UUID;

public interface ActivityAuthorizationRepository extends StaffAuthorizationRepository {
  boolean existsByStaffIdAndTargetInstitutionIds(UUID staffId, Collection<UUID> institutionIds);

  PagedResult<ActivityAuthorizationSummary> findPageByStaffIdAndTargetInstitutionIds(
      UUID staffId, Collection<UUID> institutionIds, PageCriteria pageCriteria);
}
