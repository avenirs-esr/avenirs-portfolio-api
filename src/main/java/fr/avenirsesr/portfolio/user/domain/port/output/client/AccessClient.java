package fr.avenirsesr.portfolio.user.domain.port.output.client;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import java.util.List;
import java.util.UUID;

public interface AccessClient {

  boolean staffHasAccess(String eppn, List<UUID> targetInstitutionIds, List<UUID> targetGroupIds);

  StudentAccessScope getStudentScope(String eppn);

  PagedResult<String> searchStaffEppns(String eppn, String keyword, PageCriteria pageCriteria);
}
