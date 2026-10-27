package fr.avenirsesr.portfolio.authorization.domain.port.input;

import fr.avenirsesr.portfolio.authorization.domain.data.UserEntityAuthorizationsData;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorizationEntityType;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserAuthorizationQueryService {
  List<EAuthorizationEntityType> getAvailableTypes(String eppn, Set<UUID> institutionIds);

  PagedResult<UserEntityAuthorizationsData> getAuthorizations(
      String eppn,
      EAuthorizationEntityType entityType,
      Set<UUID> institutionIds,
      PageCriteria pageCriteria);
}
