package fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.user.domain.model.Staff;

public interface StaffActivityOverviewRepository {
  PagedResult<ActivityStaffOverviewData> findAllByAuthorAndStatus(
      Staff author, EActivityStatus activityStatus, PageCriteria pageCriteria);
}
