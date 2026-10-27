package fr.avenirsesr.portfolio.authorization.domain.port.output;

import fr.avenirsesr.portfolio.authorization.domain.data.UserEntityAuthorizationsData;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorizationEntityType;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import java.util.Set;
import java.util.UUID;

public interface UserEntityAuthorizationReader {
  EAuthorizationEntityType entityType();

  boolean hasAny(UUID staffId, Set<UUID> institutionIds);

  PagedResult<UserEntityAuthorizationsData> findPage(
      UUID staffId, Set<UUID> institutionIds, PageCriteria pageCriteria);
}
