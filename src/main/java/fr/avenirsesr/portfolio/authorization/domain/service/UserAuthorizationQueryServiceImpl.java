package fr.avenirsesr.portfolio.authorization.domain.service;

import fr.avenirsesr.portfolio.authorization.domain.data.UserEntityAuthorizationsData;
import fr.avenirsesr.portfolio.authorization.domain.port.input.UserAuthorizationQueryService;
import fr.avenirsesr.portfolio.authorization.domain.port.output.UserEntityAuthorizationReader;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorizationEntityType;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserAuthorizationQueryServiceImpl implements UserAuthorizationQueryService {
  private final Map<EAuthorizationEntityType, UserEntityAuthorizationReader> readers;
  private final UserPrincipalRepository userPrincipalRepository;

  public UserAuthorizationQueryServiceImpl(
      List<UserEntityAuthorizationReader> readers,
      UserPrincipalRepository userPrincipalRepository) {
    this.readers = new EnumMap<>(EAuthorizationEntityType.class);
    this.readers.putAll(
        readers.stream()
            .collect(
                Collectors.toMap(UserEntityAuthorizationReader::entityType, Function.identity())));
    this.userPrincipalRepository = userPrincipalRepository;
  }

  @Override
  public List<EAuthorizationEntityType> getAvailableTypes(String eppn, Set<UUID> institutionIds) {
    Optional<UUID> staffId = findUserId(eppn);
    if (staffId.isEmpty() || institutionIds.isEmpty()) {
      return List.of();
    }
    return readers.values().stream()
        .filter(reader -> reader.hasAny(staffId.get(), institutionIds))
        .map(UserEntityAuthorizationReader::entityType)
        .toList();
  }

  @Override
  public PagedResult<UserEntityAuthorizationsData> getAuthorizations(
      String eppn,
      EAuthorizationEntityType entityType,
      Set<UUID> institutionIds,
      PageCriteria pageCriteria) {
    UserEntityAuthorizationReader reader = readers.get(entityType);
    if (reader == null) {
      throw new FieldValidationException(EErrorCode.INVALID_ARGUMENT_TYPE, "entityType");
    }
    Optional<UUID> staffId = findUserId(eppn);
    if (staffId.isEmpty() || institutionIds.isEmpty()) {
      return new PagedResult<>(
          List.of(), new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), 0));
    }
    return reader.findPage(staffId.get(), institutionIds, pageCriteria);
  }

  private Optional<UUID> findUserId(String eppn) {
    return userPrincipalRepository.findByEppn(eppn).map(User::getId);
  }
}
