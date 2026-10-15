package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StaffFixture;
import java.util.EnumSet;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ActivityDraftAuthorizationMapperTest {

  @Test
  void shouldMapDomainToDraftEntityAndBack() {
    BddLogger.given("an authorization held on an activity draft");
    var staff = StaffFixture.create().toModel();
    var draftId = UUID.randomUUID();
    var authorization =
        ActivityAuthorization.create(
            draftId, staff, EnumSet.of(EActivityRight.EDIT, EActivityRight.PUBLISH));

    BddLogger.when("mapping it to the draft entity and back to the domain");
    var entity = ActivityDraftAuthorizationMapper.INSTANCE.fromDomain(authorization);
    var domain = ActivityDraftAuthorizationMapper.INSTANCE.toDomain(entity);

    BddLogger.then("the draft id and the rights should be preserved");
    assertThat(entity.getId()).isEqualTo(authorization.getId());
    assertThat(entity.getActivityDraftId()).isEqualTo(draftId);
    assertThat(entity.getStaff().getId()).isEqualTo(staff.getId());
    assertThat(entity.getRights())
        .containsExactlyInAnyOrder(EActivityRight.EDIT, EActivityRight.PUBLISH);
    assertThat(domain.getActivityId()).isEqualTo(draftId);
    assertThat(domain.getStaff()).isEqualTo(staff);
    assertThat(domain.getRights())
        .containsExactlyInAnyOrder(EActivityRight.EDIT, EActivityRight.PUBLISH);
    assertThat(domain.getCreatedAt()).isEqualTo(authorization.getCreatedAt());
  }

  @Test
  void shouldMapEmptyRights() {
    BddLogger.given("a draft authorization without rights");
    var authorization =
        ActivityAuthorization.create(UUID.randomUUID(), StaffFixture.create().toModel(), null);

    BddLogger.when("mapping it to the draft entity and back");
    var domain =
        ActivityDraftAuthorizationMapper.INSTANCE.toDomain(
            ActivityDraftAuthorizationMapper.INSTANCE.fromDomain(authorization));

    BddLogger.then("the rights should be empty on both sides");
    assertThat(domain.getRights()).isEmpty();
    assertThat(domain.isLeader()).isFalse();
  }
}
