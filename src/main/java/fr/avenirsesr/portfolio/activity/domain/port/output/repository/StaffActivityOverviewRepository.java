package fr.avenirsesr.portfolio.activity.domain.port.output.repository;

import fr.avenirsesr.portfolio.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.model.Staff;

public interface StaffActivityOverviewRepository {
  PagedResult<ActivityStaffOverviewData> findAllByAuthor(Staff author, PageCriteria pageCriteria);
}
