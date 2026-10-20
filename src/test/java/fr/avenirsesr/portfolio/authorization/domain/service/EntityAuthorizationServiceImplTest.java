package fr.avenirsesr.portfolio.authorization.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.authorization.domain.data.EntityAuthorizationsData;
import fr.avenirsesr.portfolio.authorization.domain.model.enums.EAuthorization;
import fr.avenirsesr.portfolio.authorization.domain.model.enums.EAuthorizationEntityType;
import fr.avenirsesr.portfolio.authorization.domain.port.input.EntityAuthorizationService;
import fr.avenirsesr.portfolio.authorization.domain.port.output.EntityAuthorizationResolver;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EntityAuthorizationServiceImplTest {

  @Mock private EntityAuthorizationResolver activityResolver;
  @Mock private EntityAuthorizationResolver draftResolver;

  private EntityAuthorizationServiceImpl service;

  @BeforeEach
  void setUp() {
    lenient().when(activityResolver.entityType()).thenReturn(EAuthorizationEntityType.ACTIVITY);
    lenient().when(draftResolver.entityType()).thenReturn(EAuthorizationEntityType.ACTIVITY_DRAFT);
    service = new EntityAuthorizationServiceImpl(List.of(activityResolver, draftResolver));
  }

  @Test
  void shouldFailFastWhenAnEntityTypeHasNoResolver() {
    BddLogger.given("resolvers that do not cover every entity type");

    BddLogger.then("building the service should fail");
    assertThatThrownBy(() -> new EntityAuthorizationServiceImpl(List.of(activityResolver)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining(EAuthorizationEntityType.ACTIVITY_DRAFT.name());
  }

  @Test
  void shouldReturnOneEntryPerRequestedIdIncludingThoseWithoutAuthorization() {
    BddLogger.given("a resolver granting authorizations on the first activity only");
    var granted = UUID.randomUUID();
    var notGranted = UUID.randomUUID();
    when(activityResolver.resolve(Set.of(granted, notGranted)))
        .thenReturn(Map.of(granted, Set.of(EAuthorization.EDIT, EAuthorization.PUBLISH)));

    BddLogger.when("getting the authorizations of both activities");
    var result =
        service.getAuthorizations(EAuthorizationEntityType.ACTIVITY, List.of(granted, notGranted));

    BddLogger.then("both should be returned, the second one without authorization");
    assertThat(result)
        .containsExactly(
            new EntityAuthorizationsData(
                granted, Set.of(EAuthorization.EDIT, EAuthorization.PUBLISH)),
            new EntityAuthorizationsData(notGranted, Set.of()));
  }

  @Test
  void shouldResolveDistinctIdsOnceAndKeepTheRequestOrder() {
    BddLogger.given("a request containing a duplicated id");
    var first = UUID.randomUUID();
    var second = UUID.randomUUID();
    when(activityResolver.resolve(Set.of(second, first))).thenReturn(Map.of());

    BddLogger.when("getting the authorizations");
    var result =
        service.getAuthorizations(
            EAuthorizationEntityType.ACTIVITY, List.of(second, first, second));

    BddLogger.then("each id should be returned once, in request order");
    assertThat(result)
        .extracting(EntityAuthorizationsData::entityId)
        .containsExactly(second, first);
  }

  @Test
  void shouldUseTheResolverOfTheRequestedEntityType() {
    BddLogger.given("a draft id");
    var draftId = UUID.randomUUID();
    when(draftResolver.resolve(Set.of(draftId)))
        .thenReturn(Map.of(draftId, Set.of(EAuthorization.EDIT)));

    BddLogger.when("getting the authorizations of a draft");
    var result =
        service.getAuthorizations(EAuthorizationEntityType.ACTIVITY_DRAFT, List.of(draftId));

    BddLogger.then("only the draft resolver should have been used");
    assertThat(result)
        .containsExactly(new EntityAuthorizationsData(draftId, Set.of(EAuthorization.EDIT)));
    verify(activityResolver, never()).resolve(Set.of(draftId));
  }

  @Test
  void shouldRejectARequestWithoutIds() {
    BddLogger.when("getting the authorizations of no entity");

    BddLogger.then("the request should be rejected and nothing resolved");
    assertThatThrownBy(
            () -> service.getAuthorizations(EAuthorizationEntityType.ACTIVITY, List.of()))
        .isInstanceOf(FieldValidationException.class)
        .hasFieldOrPropertyWithValue("errorCode", EErrorCode.NOT_BLANK);
    verify(activityResolver, never()).resolve(anyCollection());
  }

  @Test
  void shouldRejectARequestWithTooManyIds() {
    BddLogger.given("more ids than the maximum allowed");
    var ids =
        IntStream.rangeClosed(0, EntityAuthorizationService.MAX_ENTITY_IDS)
            .mapToObj(i -> UUID.randomUUID())
            .toList();

    BddLogger.then("the request should be rejected and nothing resolved");
    assertThatThrownBy(() -> service.getAuthorizations(EAuthorizationEntityType.ACTIVITY, ids))
        .isInstanceOf(FieldValidationException.class)
        .hasFieldOrPropertyWithValue("errorCode", EErrorCode.TOO_LONG);
    verify(activityResolver, never()).resolve(anyCollection());
  }

  @Test
  void shouldRejectARequestContainingANullId() {
    BddLogger.given("a request containing a null id");
    var ids = Arrays.asList(UUID.randomUUID(), null);

    BddLogger.then("the request should be rejected and nothing resolved");
    assertThatThrownBy(() -> service.getAuthorizations(EAuthorizationEntityType.ACTIVITY, ids))
        .isInstanceOf(FieldValidationException.class)
        .hasFieldOrPropertyWithValue("errorCode", EErrorCode.NOT_NULL);
    verify(activityResolver, never()).resolve(anyCollection());
  }

  @Test
  void shouldReportTheListAccessOfTheRequestedEntityType() {
    BddLogger.given("a resolver granting list access on activities only");
    when(activityResolver.hasAuthorizationOnAny()).thenReturn(true);
    when(draftResolver.hasAuthorizationOnAny()).thenReturn(false);

    BddLogger.then("each entity type should report its own list access");
    assertThat(service.canAccessList(EAuthorizationEntityType.ACTIVITY)).isTrue();
    assertThat(service.canAccessList(EAuthorizationEntityType.ACTIVITY_DRAFT)).isFalse();
  }
}
