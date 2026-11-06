package fr.avenirsesr.portfolio.staff.activity.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StaffFixture;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ActivityNodeTest {

  private static final UUID MAIN_ACTIVITY_ID = UUID.randomUUID();

  @Test
  void shouldBeAMainActivityWhenItIsLinkedToNoOtherActivity() {
    BddLogger.given("a draft that is not linked to any other activity");
    var draft = ActivityDraft.create("Main", StaffFixture.create().toModel());

    BddLogger.then("it should not be a sub-activity");
    assertFalse(draft.isSubActivity());
  }

  @Test
  void shouldBeASubActivityWhenItIsAttachedToAMainActivity() {
    BddLogger.given("a draft attached to a main activity");
    var draft = ActivityDraft.create("Sub", StaffFixture.create().toModel());
    draft.attachAsSubActivity(MAIN_ACTIVITY_ID, MAIN_ACTIVITY_ID);

    BddLogger.then("it should be a sub-activity of that main activity");
    assertTrue(draft.isSubActivity());
    assertEquals(MAIN_ACTIVITY_ID, draft.getMainActivityId().orElseThrow());
    assertEquals(MAIN_ACTIVITY_ID, draft.getPreviousActivityId().orElseThrow());
  }

  @Test
  void shouldOrderTheSubActivitiesByFollowingTheChainFromTheMainActivity() {
    BddLogger.given("three sub-activities given in a random order");
    var first = subActivity(MAIN_ACTIVITY_ID);
    var second = subActivity(first.getId());
    var third = subActivity(second.getId());

    BddLogger.when("ordering them from the main activity");
    var ordered = ActivityNode.orderedFrom(MAIN_ACTIVITY_ID, List.of(third, first, second));

    BddLogger.then("they should follow the chain of previous activities");
    assertEquals(List.of(first, second, third), ordered);
  }

  @Test
  void shouldKeepTheSubActivitiesOutsideTheChainAtTheEnd() {
    BddLogger.given("a sub-activity whose previous activity is missing");
    var first = subActivity(MAIN_ACTIVITY_ID);
    var orphan = subActivity(UUID.randomUUID());

    BddLogger.when("ordering them from the main activity");
    var ordered = ActivityNode.orderedFrom(MAIN_ACTIVITY_ID, List.of(orphan, first));

    BddLogger.then("the orphan should be kept after the chain");
    assertEquals(List.of(first, orphan), ordered);
  }

  private ActivityDraft subActivity(UUID previousActivityId) {
    var draft = ActivityDraft.create("Sub", StaffFixture.create().toModel());
    draft.attachAsSubActivity(MAIN_ACTIVITY_ID, previousActivityId);
    return draft;
  }
}
