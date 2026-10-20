package fr.avenirsesr.portfolio.authorization.domain.service;

import fr.avenirsesr.portfolio.authorization.domain.data.EntityAuthorizationsData;
import fr.avenirsesr.portfolio.authorization.domain.model.enums.EAuthorizationEntityType;
import fr.avenirsesr.portfolio.authorization.domain.port.input.EntityAuthorizationService;
import fr.avenirsesr.portfolio.authorization.domain.port.output.EntityAuthorizationResolver;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityAuthorizationServiceImpl implements EntityAuthorizationService {
  private final Map<EAuthorizationEntityType, EntityAuthorizationResolver> resolvers;

  public EntityAuthorizationServiceImpl(List<EntityAuthorizationResolver> resolvers) {
    this.resolvers = new EnumMap<>(EAuthorizationEntityType.class);
    this.resolvers.putAll(
        resolvers.stream()
            .collect(
                Collectors.toMap(EntityAuthorizationResolver::entityType, Function.identity())));
    for (var entityType : EAuthorizationEntityType.values()) {
      if (!this.resolvers.containsKey(entityType)) {
        throw new IllegalStateException("No authorization resolver for entity type " + entityType);
      }
    }
  }

  @Override
  public List<EntityAuthorizationsData> getAuthorizations(
      EAuthorizationEntityType entityType, Collection<UUID> entityIds) {
    validate(entityIds);
    var distinctIds = new LinkedHashSet<>(entityIds);
    var authorizationsById = resolvers.get(entityType).resolve(distinctIds);
    return distinctIds.stream()
        .map(id -> new EntityAuthorizationsData(id, authorizationsById.getOrDefault(id, Set.of())))
        .toList();
  }

  private void validate(Collection<UUID> entityIds) {
    FieldValidationUtils.requireNotNull("entityIds", entityIds);
    if (entityIds.isEmpty()) {
      throw new FieldValidationException(EErrorCode.NOT_BLANK, "entityIds");
    }
    if (entityIds.size() > MAX_ENTITY_IDS) {
      throw new FieldValidationException(EErrorCode.TOO_LONG, "entityIds");
    }
    if (entityIds.stream().anyMatch(Objects::isNull)) {
      throw new FieldValidationException(EErrorCode.NOT_NULL, "entityIds");
    }
  }

  @Override
  public boolean canAccessList(EAuthorizationEntityType entityType) {
    return resolvers.get(entityType).hasAuthorizationOnAny();
  }
}
