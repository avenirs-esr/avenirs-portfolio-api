package fr.avenirsesr.portfolio.authorization.domain.port.output;

import fr.avenirsesr.portfolio.authorization.domain.model.enums.EAuthorization;
import fr.avenirsesr.portfolio.authorization.domain.model.enums.EAuthorizationEntityType;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface EntityAuthorizationResolver {
  EAuthorizationEntityType entityType();

  Map<UUID, Set<EAuthorization>> resolve(Collection<UUID> entityIds);

  boolean hasAuthorizationOnAny();
}
