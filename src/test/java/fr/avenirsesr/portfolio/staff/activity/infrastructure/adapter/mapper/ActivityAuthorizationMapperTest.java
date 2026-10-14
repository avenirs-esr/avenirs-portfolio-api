package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StaffFixture;
import java.util.EnumSet;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ActivityAuthorizationMapperTest {

  @Test
  void shouldMapDomainToEntityAndBack() {
    BddLogger.given("an activity authorization");
    var staff = StaffFixture.create().toModel();
    var activityId = UUID.randomUUID();
    var authorization =
        ActivityAuthorization.create(
            activityId, staff, EnumSet.of(EActivityRight.RESPONSIBLE, EActivityRight.FEEDBACK));

    BddLogger.when("mapping it to an entity and back to the domain");
    var entity = ActivityAuthorizationMapper.INSTANCE.fromDomain(authorization);
    var domain = ActivityAuthorizationMapper.INSTANCE.toDomain(entity);

    BddLogger.then("every field should be preserved");
    assertThat(entity.getId()).isEqualTo(authorization.getId());
    assertThat(entity.getActivityId()).isEqualTo(activityId);
    assertThat(entity.getStaff().getId()).isEqualTo(staff.getId());
    assertThat(entity.getRights())
        .containsExactlyInAnyOrder(EActivityRight.RESPONSIBLE, EActivityRight.FEEDBACK);
    assertThat(domain.getId()).isEqualTo(authorization.getId());
    assertThat(domain.getActivityId()).isEqualTo(activityId);
    assertThat(domain.getStaff()).isEqualTo(staff);
    assertThat(domain.getRights())
        .containsExactlyInAnyOrder(EActivityRight.RESPONSIBLE, EActivityRight.FEEDBACK);
    assertThat(domain.getCreatedAt()).isEqualTo(authorization.getCreatedAt());
  }

  @Test
  void shouldMapEmptyRights() {
    BddLogger.given("an activity authorization without rights");
    var authorization =
        ActivityAuthorization.create(UUID.randomUUID(), StaffFixture.create().toModel(), null);

    BddLogger.when("mapping it to an entity and back");
    var domain =
        ActivityAuthorizationMapper.INSTANCE.toDomain(
            ActivityAuthorizationMapper.INSTANCE.fromDomain(authorization));

    BddLogger.then("the rights should be empty on both sides");
    assertThat(domain.getRights()).isEmpty();
    assertThat(domain.isResponsible()).isFalse();
  }
}
