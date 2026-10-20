package fr.avenirsesr.portfolio.authorization.domain.port.input;

import fr.avenirsesr.portfolio.authorization.domain.data.EntityAuthorizationsData;
import fr.avenirsesr.portfolio.authorization.domain.model.enums.EAuthorizationEntityType;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EntityAuthorizationService {
  /**
   * Maximum number of entity ids per request (duplicates included), to bound the processing cost.
   * Also enforced on the REST request: API clients must not send more.
   */
  int MAX_ENTITY_IDS = 100;

  List<EntityAuthorizationsData> getAuthorizations(
      EAuthorizationEntityType entityType, Collection<UUID> entityIds);

  boolean canAccessList(EAuthorizationEntityType entityType);
}
