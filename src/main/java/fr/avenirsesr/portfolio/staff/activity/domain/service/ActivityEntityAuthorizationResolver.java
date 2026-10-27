package fr.avenirsesr.portfolio.staff.activity.domain.service;

import fr.avenirsesr.portfolio.authorization.domain.port.output.EntityAuthorizationResolver;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorization;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorizationEntityType;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ActivityEntityAuthorizationResolver implements EntityAuthorizationResolver {
  private final EAuthorizationEntityType entityType;
  private final Function<Collection<UUID>, Map<UUID, Set<EActivityRight>>> rightsByEntityId;
  private final BooleanSupplier hasAnyRight;

  public ActivityEntityAuthorizationResolver(
      EAuthorizationEntityType entityType,
      Function<Collection<UUID>, Map<UUID, Set<EActivityRight>>> rightsByEntityId,
      BooleanSupplier hasAnyRight) {
    this.entityType = entityType;
    this.rightsByEntityId = rightsByEntityId;
    this.hasAnyRight = hasAnyRight;
  }

  @Override
  public EAuthorizationEntityType entityType() {
    return entityType;
  }

  @Override
  public Map<UUID, Set<EAuthorization>> resolve(Collection<UUID> entityIds) {
    return rightsByEntityId.apply(entityIds).entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry ->
                    entry.getValue().stream()
                        .map(right -> EAuthorization.valueOf(right.name()))
                        .collect(Collectors.toSet())));
  }

  @Override
  public boolean hasAuthorizationOnAny() {
    return hasAnyRight.getAsBoolean();
  }
}
