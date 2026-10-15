package fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.StaffInfoDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.request.ActivityAuthorizationsRequest;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.request.StaffAuthorizationRequest;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityAuthorizationDefinition;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityAuthorizationDtoMapperTest {

  @Spy private StaffInfoDtoMapper staffInfoDtoMapper = Mappers.getMapper(StaffInfoDtoMapper.class);
  @InjectMocks private ActivityAuthorizationDtoMapperImpl mapper;

  private Staff aStaff(String firstName, String lastName) {
    var user = UserFixture.create().withFirstName(firstName).withLastName(lastName).toModel();
    return Staff.create(user, "staff@email.com", "bio");
  }

  @Test
  void shouldMapAuthorizationsToDTOs() {
    BddLogger.given("two authorizations on an activity");
    var activityId = UUID.randomUUID();
    var leader = aStaff("Camille", "Fournier");
    var reviewer = aStaff("Zoé", "Cousin");
    var authorizations =
        List.of(
            ActivityAuthorization.create(activityId, leader, EActivityRight.all()),
            ActivityAuthorization.create(
                activityId, reviewer, EnumSet.of(EActivityRight.FEEDBACK)));

    BddLogger.when("mapping them to DTOs");
    var dtos = mapper.toDTOs(authorizations);

    BddLogger.then("each DTO should carry the staff info and its rights in enum order");
    assertThat(dtos).hasSize(2);
    assertThat(dtos.getFirst().staff())
        .isEqualTo(new StaffInfoDTO(leader.getId(), "Camille", "Fournier"));
    assertThat(dtos.getFirst().rights()).containsExactly(EActivityRight.values());
    assertThat(dtos.get(1).staff()).isEqualTo(new StaffInfoDTO(reviewer.getId(), "Zoé", "Cousin"));
    assertThat(dtos.get(1).rights()).containsExactly(EActivityRight.FEEDBACK);
  }

  @Test
  void shouldMapTheRequestToDefinitions() {
    BddLogger.given("an authorizations request with a null entry and duplicated rights");
    var leaderId = UUID.randomUUID();
    var reviewerId = UUID.randomUUID();
    var request =
        new ActivityAuthorizationsRequest(
            Arrays.asList(
                new StaffAuthorizationRequest(leaderId, Arrays.asList(EActivityRight.LEADER, null)),
                null,
                new StaffAuthorizationRequest(
                    reviewerId, List.of(EActivityRight.FEEDBACK, EActivityRight.FEEDBACK)),
                new StaffAuthorizationRequest(UUID.randomUUID(), null)));

    BddLogger.when("mapping it to definitions");
    var definitions = mapper.toDefinitions(request);

    BddLogger.then("null entries are dropped and rights are deduplicated");
    assertThat(definitions)
        .containsExactly(
            new ActivityAuthorizationDefinition(leaderId, EnumSet.of(EActivityRight.LEADER)),
            new ActivityAuthorizationDefinition(reviewerId, EnumSet.of(EActivityRight.FEEDBACK)),
            new ActivityAuthorizationDefinition(
                definitions.get(2).staffId(), EnumSet.noneOf(EActivityRight.class)));
  }

  @Test
  void shouldMapAnEmptyRequestToNoDefinition() {
    BddLogger.given("an empty request");
    BddLogger.when("mapping it to definitions");
    BddLogger.then("no definition should be returned");
    assertThat(mapper.toDefinitions(null)).isEmpty();
    assertThat(mapper.toDefinitions(new ActivityAuthorizationsRequest(null))).isEmpty();
  }

  @Test
  void shouldMapANullStaffToANullInfo() {
    BddLogger.given("a null staff");
    BddLogger.when("mapping it to a staff info");
    BddLogger.then("null should be returned");
    assertThat(staffInfoDtoMapper.toDTO(null)).isNull();
  }
}
