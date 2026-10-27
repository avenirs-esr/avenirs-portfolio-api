package fr.avenirsesr.portfolio.staff.activity.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorization;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorizationEntityType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ActivityEntityAuthorizationResolverTest {

  @Test
  void shouldExposeTheEntityTypeItIsBoundTo() {
    BddLogger.given("a resolver bound to the draft entity type");
    var resolver =
        new ActivityEntityAuthorizationResolver(
            EAuthorizationEntityType.ACTIVITY_DRAFT, ids -> Map.of(), () -> false);

    BddLogger.then("it should expose that entity type");
    assertThat(resolver.entityType()).isEqualTo(EAuthorizationEntityType.ACTIVITY_DRAFT);
  }

  @Test
  void shouldExposeEveryActivityRightAsAnAuthorization() {
    BddLogger.given("a resolver granting every activity right");
    var entityId = UUID.randomUUID();
    var resolver =
        new ActivityEntityAuthorizationResolver(
            EAuthorizationEntityType.ACTIVITY,
            ids -> Map.of(entityId, EActivityRight.all()),
            () -> true);

    BddLogger.then("each right should map to the authorization of the same name");
    assertThat(resolver.resolve(List.of(entityId)).get(entityId))
        .containsExactlyInAnyOrderElementsOf(EnumSet.allOf(EAuthorization.class));
  }

  @Test
  void shouldResolveTheRightsOfEachEntityAsAuthorizationNames() {
    BddLogger.given("a resolver reading rights for two entities");
    var firstId = UUID.randomUUID();
    var secondId = UUID.randomUUID();
    var resolver =
        new ActivityEntityAuthorizationResolver(
            EAuthorizationEntityType.ACTIVITY,
            ids ->
                Map.of(
                    firstId,
                    EnumSet.of(EActivityRight.EDIT, EActivityRight.MANAGE_AUTHORIZATIONS),
                    secondId,
                    EnumSet.of(EActivityRight.PUBLISH)),
            () -> true);

    BddLogger.when("resolving the authorizations");
    var resolved = resolver.resolve(List.of(firstId, secondId));

    BddLogger.then("each entity should map to its authorization names");
    assertThat(resolved)
        .containsEntry(firstId, Set.of(EAuthorization.EDIT, EAuthorization.MANAGE_AUTHORIZATIONS))
        .containsEntry(secondId, Set.of(EAuthorization.PUBLISH))
        .hasSize(2);
  }

  @Test
  void shouldResolveNothingWhenNoRightIsHeld() {
    BddLogger.given("a resolver whose source holds no right");
    var resolver =
        new ActivityEntityAuthorizationResolver(
            EAuthorizationEntityType.ACTIVITY, ids -> Map.of(), () -> false);

    BddLogger.then("the result should be empty");
    assertThat(resolver.resolve(List.of(UUID.randomUUID()))).isEmpty();
  }

  @Test
  void shouldDelegateTheListAccessToItsSource() {
    BddLogger.given("two resolvers backed by opposite list access sources");
    var granted =
        new ActivityEntityAuthorizationResolver(
            EAuthorizationEntityType.ACTIVITY, ids -> Map.of(), () -> true);
    var denied =
        new ActivityEntityAuthorizationResolver(
            EAuthorizationEntityType.ACTIVITY, ids -> Map.of(), () -> false);

    BddLogger.then("each one should report its source answer");
    assertThat(granted.hasAuthorizationOnAny()).isTrue();
    assertThat(denied.hasAuthorizationOnAny()).isFalse();
  }
}
