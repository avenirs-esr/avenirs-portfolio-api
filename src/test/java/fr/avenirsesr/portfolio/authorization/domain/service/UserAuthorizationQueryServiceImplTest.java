package fr.avenirsesr.portfolio.authorization.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.authorization.domain.data.UserEntityAuthorizationsData;
import fr.avenirsesr.portfolio.authorization.domain.port.output.UserEntityAuthorizationReader;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorization;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorizationEntityType;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAuthorizationQueryServiceImplTest {
  private static final String EPPN = "jane@univ.fr";

  @Mock private UserPrincipalRepository userPrincipalRepository;
  @Mock private UserEntityAuthorizationReader activityReader;

  private final UUID staffId = UUID.randomUUID();
  private final Set<UUID> institutionIds = Set.of(UUID.randomUUID());
  private final PageCriteria pageCriteria = new PageCriteria(0, 8);

  private UserAuthorizationQueryServiceImpl service;

  @BeforeEach
  void setUp() {
    lenient().when(activityReader.entityType()).thenReturn(EAuthorizationEntityType.ACTIVITY);
    service =
        new UserAuthorizationQueryServiceImpl(List.of(activityReader), userPrincipalRepository);
  }

  private void givenKnownUser() {
    var user = mock(User.class);
    when(user.getId()).thenReturn(staffId);
    when(userPrincipalRepository.findByEppn(EPPN)).thenReturn(Optional.of(user));
  }

  @Test
  void shouldListOnlyTheTypesHoldingAnAuthorization() {
    BddLogger.given("a user holding an authorization on an activity");
    givenKnownUser();
    when(activityReader.hasAny(staffId, institutionIds)).thenReturn(true);

    BddLogger.when("getting the available types");
    var types = service.getAvailableTypes(EPPN, institutionIds);

    BddLogger.then("only the activity type should be available");
    assertThat(types).containsExactly(EAuthorizationEntityType.ACTIVITY);
  }

  @Test
  void shouldNotListATypeWithoutAuthorization() {
    BddLogger.given("a user without any authorization");
    givenKnownUser();
    when(activityReader.hasAny(staffId, institutionIds)).thenReturn(false);

    BddLogger.when("getting the available types");
    var types = service.getAvailableTypes(EPPN, institutionIds);

    BddLogger.then("no type should be available");
    assertThat(types).isEmpty();
  }

  @Test
  void shouldReturnNoTypeNorErrorForAnUnknownUser() {
    BddLogger.given("an eppn that never logged in");
    when(userPrincipalRepository.findByEppn(EPPN)).thenReturn(Optional.empty());

    BddLogger.when("getting the available types and the authorizations");
    var types = service.getAvailableTypes(EPPN, institutionIds);
    var page =
        service.getAuthorizations(
            EPPN, EAuthorizationEntityType.ACTIVITY, institutionIds, pageCriteria);

    BddLogger.then("both should be empty, without querying the readers");
    assertThat(types).isEmpty();
    assertThat(page.content()).isEmpty();
    assertThat(page.pageInfo().totalElements()).isZero();
    verify(activityReader, never()).findPage(staffId, institutionIds, pageCriteria);
  }

  @Test
  void shouldDelegateThePageToTheReaderOfTheType() {
    BddLogger.given("a reader returning one activity");
    givenKnownUser();
    var expected =
        new PagedResult<>(
            List.of(
                new UserEntityAuthorizationsData(
                    UUID.randomUUID(), "My activity", Set.of(EAuthorization.EDIT))),
            new PageInfo(0, 8, 1));
    when(activityReader.findPage(staffId, institutionIds, pageCriteria)).thenReturn(expected);

    BddLogger.when("getting the authorizations on activities");
    var page =
        service.getAuthorizations(
            EPPN, EAuthorizationEntityType.ACTIVITY, institutionIds, pageCriteria);

    BddLogger.then("the page of the reader should be returned");
    assertThat(page).isEqualTo(expected);
  }

  @Test
  void shouldRejectATypeThatCannotBeConsulted() {
    BddLogger.given("a type without reader");

    BddLogger.then("getting its authorizations should be rejected");
    assertThatThrownBy(
            () ->
                service.getAuthorizations(
                    EPPN, EAuthorizationEntityType.ACTIVITY_DRAFT, institutionIds, pageCriteria))
        .isInstanceOf(FieldValidationException.class);
  }
}
