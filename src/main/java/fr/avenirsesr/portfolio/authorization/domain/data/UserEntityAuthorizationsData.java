package fr.avenirsesr.portfolio.authorization.domain.data;

import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorization;
import java.util.Set;
import java.util.UUID;

public record UserEntityAuthorizationsData(
    UUID entityId, String entityName, Set<EAuthorization> authorizations) {}
