package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StaffFixture;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ActivityDraftMapperTest {

  @Test
  void shouldMapTheSubActivityLinkBothWays() {
    BddLogger.given("a sub-activity draft linked to its main draft and to its previous draft");
    var draft = ActivityDraft.create("Step", StaffFixture.create().toModel());
    UUID mainActivityId = UUID.randomUUID();
    UUID previousActivityId = UUID.randomUUID();
    draft.attachAsSubActivity(mainActivityId, previousActivityId);

    BddLogger.when("mapping it to an entity and back");
    var entity = ActivityDraftMapper.INSTANCE.fromDomain(draft);
    var mappedDraft = ActivityDraftMapper.INSTANCE.toDomain(entity);

    BddLogger.then("the link should be kept on both sides");
    assertEquals(mainActivityId, entity.getMainActivityId());
    assertEquals(previousActivityId, entity.getPreviousActivityId());
    assertTrue(mappedDraft.isSubActivity());
    assertEquals(mainActivityId, mappedDraft.getMainActivityId().orElseThrow());
    assertEquals(previousActivityId, mappedDraft.getPreviousActivityId().orElseThrow());
  }

  @Test
  void shouldMapAMainDraftWithoutLink() {
    BddLogger.given("a main draft");
    var draft = ActivityDraft.create("Main", StaffFixture.create().toModel());

    BddLogger.when("mapping it to an entity and back");
    var entity = ActivityDraftMapper.INSTANCE.fromDomain(draft);
    var mappedDraft = ActivityDraftMapper.INSTANCE.toDomain(entity);

    BddLogger.then("no link should be set");
    assertNull(entity.getMainActivityId());
    assertNull(entity.getPreviousActivityId());
    assertFalse(mappedDraft.isSubActivity());
  }
}
