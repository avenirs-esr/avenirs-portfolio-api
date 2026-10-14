package fr.avenirsesr.portfolio.staff.activity.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityAuthorizationDefinition;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityResponsibleRequiredException;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityAuthorizationRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStaffException;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StaffFixture;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityAuthorizationServiceImplTest {

  @Mock private ActivityAuthorizationRepository activityAuthorizationRepository;
  @Mock private ActivityRepository activityRepository;
  @Mock private ActivityDraftRepository activityDraftRepository;
  @Mock private StaffRepository staffRepository;
  @Mock private LoggedInUserService loggedInUserService;

  @InjectMocks private ActivityAuthorizationServiceImpl service;

  private UUID activityId;
  private Staff staff;

  @BeforeEach
  void setUp() {
    activityId = UUID.randomUUID();
    staff = StaffFixture.create().toModel();
  }

  private ActivityAuthorization authorizationOf(Staff holder, EActivityRight... rights) {
    return ActivityAuthorization.create(activityId, holder, Set.of(rights));
  }

  private void givenAuthorization(ActivityAuthorization authorization) {
    when(activityAuthorizationRepository.findByActivityIdAndStaffId(
            activityId, authorization.getStaff().getId()))
        .thenReturn(Optional.of(authorization));
  }

  private void givenNoAuthorization(Staff holder) {
    when(activityAuthorizationRepository.findByActivityIdAndStaffId(activityId, holder.getId()))
        .thenReturn(Optional.empty());
  }

  @Nested
  class WhenCheckingARight {

    @BeforeEach
    void setupWhen() {
      BddLogger.given("an activity authorization service");
      BddLogger.when("checking whether a staff holds a right on an activity");
    }

    @Test
    void thenItShouldBeGrantedWhenTheAuthorizationHoldsTheRight() {
      BddLogger.then("the right should be granted");
      givenAuthorization(authorizationOf(staff, EActivityRight.EDIT, EActivityRight.PUBLISH));

      assertThat(service.hasRight(staff, activityId, EActivityRight.EDIT)).isTrue();
      assertThat(service.hasRight(staff, activityId, EActivityRight.FEEDBACK)).isFalse();
    }

    @Test
    void thenItShouldBeDeniedWhenTheStaffHasNoAuthorization() {
      BddLogger.then("the right should be denied");
      givenNoAuthorization(staff);

      assertThat(service.hasRight(staff, activityId, EActivityRight.EDIT)).isFalse();
      assertThat(service.hasAnyRight(staff, activityId)).isFalse();
      assertThat(service.getRights(staff, activityId)).isEmpty();
    }

    @Test
    void thenItShouldBeDeniedWhenTheStaffOrTheActivityIsMissing() {
      BddLogger.then("the right should be denied without querying the repository");

      assertThat(service.hasRight(null, activityId, EActivityRight.EDIT)).isFalse();
      assertThat(service.hasRight(staff, null, EActivityRight.EDIT)).isFalse();
      verify(activityAuthorizationRepository, never()).findByActivityIdAndStaffId(any(), any());
    }

    @Test
    void thenAnyOfTheRightsShouldBeEnough() {
      BddLogger.then("holding one of the candidate rights should be enough");
      givenAuthorization(authorizationOf(staff, EActivityRight.FEEDBACK));

      assertThat(service.hasAnyRight(staff, activityId)).isTrue();
      assertThat(
              service.hasAnyRight(
                  staff, activityId, EnumSet.of(EActivityRight.EDIT, EActivityRight.FEEDBACK)))
          .isTrue();
      assertThat(service.hasAnyRight(staff, activityId, EActivityRight.MANAGEMENT_RIGHTS))
          .isFalse();
    }

    @Test
    void thenCheckRightShouldThrowWhenTheRightIsMissing() {
      BddLogger.then("a UserNotAuthorizedException should be thrown");
      givenAuthorization(authorizationOf(staff, EActivityRight.FEEDBACK));

      assertThatThrownBy(() -> service.checkRight(staff, activityId, EActivityRight.EDIT))
          .isInstanceOf(UserNotAuthorizedException.class);
      assertThatThrownBy(
              () -> service.checkAnyRight(staff, activityId, EActivityRight.MANAGEMENT_RIGHTS))
          .isInstanceOf(UserNotAuthorizedException.class);
    }

    @Test
    void thenCheckRightShouldPassWhenTheRightIsHeld() {
      BddLogger.then("no exception should be thrown");
      givenAuthorization(authorizationOf(staff, EActivityRight.FEEDBACK));

      service.checkRight(staff, activityId, EActivityRight.FEEDBACK);
      service.checkAnyRight(staff, activityId);
      service.checkAnyRight(staff, activityId, EnumSet.of(EActivityRight.FEEDBACK));
    }

    @Test
    void thenCheckAnyRightShouldThrowWhenTheStaffHasNoAuthorization() {
      BddLogger.then("a UserNotAuthorizedException should be thrown");
      givenNoAuthorization(staff);

      assertThatThrownBy(() -> service.checkAnyRight(staff, activityId))
          .isInstanceOf(UserNotAuthorizedException.class);
    }

    @Test
    void thenGetRightsShouldReturnTheHeldRights() {
      BddLogger.then("the held rights should be returned");
      givenAuthorization(authorizationOf(staff, EActivityRight.EDIT, EActivityRight.PUBLISH));

      assertThat(service.getRights(staff, activityId))
          .containsExactlyInAnyOrder(EActivityRight.EDIT, EActivityRight.PUBLISH);
    }
  }

  @Nested
  class WhenGettingTheActivityAuthorizations {

    @BeforeEach
    void setupWhen() {
      BddLogger.given("an activity authorization service");
      BddLogger.when("getting the authorizations of an activity");
      lenient().when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    }

    @Test
    void thenItShouldReturnThemWhenTheStaffHoldsAnyRight() {
      BddLogger.then("the authorizations should be returned");
      var other = StaffFixture.create().toModel();
      var mine = authorizationOf(staff, EActivityRight.FEEDBACK);
      var theirs = authorizationOf(other, EActivityRight.RESPONSIBLE);
      when(activityRepository.existsById(activityId)).thenReturn(true);
      givenAuthorization(mine);
      when(activityAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(List.of(mine, theirs));

      assertThat(service.getActivityAuthorizations(activityId)).containsExactly(mine, theirs);
    }

    @Test
    void thenItShouldAcceptADraftId() {
      BddLogger.then("a draft id should be resolved as well");
      when(activityRepository.existsById(activityId)).thenReturn(false);
      when(activityDraftRepository.existsById(activityId)).thenReturn(true);
      givenAuthorization(authorizationOf(staff, EActivityRight.EDIT));
      when(activityAuthorizationRepository.findAllByActivityId(activityId)).thenReturn(List.of());

      assertThat(service.getActivityAuthorizations(activityId)).isEmpty();
    }

    @Test
    void thenItShouldThrowWhenNeitherAnActivityNorADraftExists() {
      BddLogger.then("an ActivityNotFoundException should be thrown");
      when(activityRepository.existsById(activityId)).thenReturn(false);
      when(activityDraftRepository.existsById(activityId)).thenReturn(false);

      assertThatThrownBy(() -> service.getActivityAuthorizations(activityId))
          .isInstanceOf(ActivityNotFoundException.class);
    }

    @Test
    void thenItShouldThrowWhenTheStaffHoldsNoRight() {
      BddLogger.then("a UserNotAuthorizedException should be thrown");
      when(activityRepository.existsById(activityId)).thenReturn(true);
      givenNoAuthorization(staff);

      assertThatThrownBy(() -> service.getActivityAuthorizations(activityId))
          .isInstanceOf(UserNotAuthorizedException.class);
    }
  }

  @Nested
  class WhenDefiningTheActivityAuthorizations {

    Staff responsible;
    Staff reviewer;

    @BeforeEach
    void setupWhen() {
      BddLogger.given("an activity authorization service");
      BddLogger.when("defining the authorizations of an activity");
      responsible = StaffFixture.create().toModel();
      reviewer = StaffFixture.create().toModel();
      lenient().when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
      lenient().when(activityRepository.existsById(activityId)).thenReturn(true);
    }

    private void givenManager() {
      givenAuthorization(authorizationOf(staff, EActivityRight.MANAGE_AUTHORIZATIONS));
    }

    private ActivityAuthorizationDefinition responsibleDefinition(Staff holder) {
      return new ActivityAuthorizationDefinition(holder.getId(), EActivityRight.all());
    }

    @Test
    void thenItShouldCreateUpdateAndRemoveAuthorizations() {
      BddLogger.then("new entries are created, kept ones updated and missing ones removed");
      givenManager();
      var removedStaff = StaffFixture.create().toModel();
      var existingReviewer = authorizationOf(reviewer, EActivityRight.FEEDBACK);
      var removed = authorizationOf(removedStaff, EActivityRight.EVALUATION);
      when(activityAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(List.of(existingReviewer, removed));
      when(staffRepository.findAllById(anyList())).thenReturn(List.of(responsible, reviewer));
      when(activityAuthorizationRepository.saveAll(anyList()))
          .thenAnswer(invocation -> invocation.getArgument(0));

      var result =
          service.defineActivityAuthorizations(
              activityId,
              List.of(
                  responsibleDefinition(responsible),
                  new ActivityAuthorizationDefinition(
                      reviewer.getId(),
                      EnumSet.of(EActivityRight.FEEDBACK, EActivityRight.EVALUATION))));

      verify(activityAuthorizationRepository).removeAllFromDatabase(List.of(removed));
      ArgumentCaptor<List<ActivityAuthorization>> captor = ArgumentCaptor.captor();
      verify(activityAuthorizationRepository).saveAll(captor.capture());
      var saved = captor.getValue();
      assertThat(saved).hasSize(2);
      assertThat(saved.getFirst().getStaff()).isEqualTo(responsible);
      assertThat(saved.getFirst().getRights())
          .containsExactlyInAnyOrderElementsOf(EActivityRight.all());
      assertThat(saved.get(1)).isSameAs(existingReviewer);
      assertThat(existingReviewer.getRights())
          .containsExactlyInAnyOrder(EActivityRight.FEEDBACK, EActivityRight.EVALUATION);
      assertThat(result).isEqualTo(saved);
    }

    @Test
    void thenItShouldMergeDuplicatedStaffEntries() {
      BddLogger.then("the rights of a staff listed twice should be merged");
      givenManager();
      when(activityAuthorizationRepository.findAllByActivityId(activityId)).thenReturn(List.of());
      when(staffRepository.findAllById(anyList())).thenReturn(List.of(responsible));
      when(activityAuthorizationRepository.saveAll(anyList()))
          .thenAnswer(invocation -> invocation.getArgument(0));

      var result =
          service.defineActivityAuthorizations(
              activityId,
              List.of(
                  new ActivityAuthorizationDefinition(
                      responsible.getId(), EActivityRight.MANAGEMENT_RIGHTS),
                  new ActivityAuthorizationDefinition(
                      responsible.getId(), EnumSet.of(EActivityRight.FEEDBACK))));

      assertThat(result).hasSize(1);
      assertThat(result.getFirst().getRights())
          .containsExactlyInAnyOrder(
              EActivityRight.RESPONSIBLE,
              EActivityRight.EDIT,
              EActivityRight.MANAGE_AUTHORIZATIONS,
              EActivityRight.PUBLISH,
              EActivityRight.FEEDBACK);
    }

    @Test
    void thenItShouldIgnoreEntriesWithoutRights() {
      BddLogger.then("an entry without rights should not create an authorization");
      givenManager();
      when(activityAuthorizationRepository.findAllByActivityId(activityId)).thenReturn(List.of());
      when(staffRepository.findAllById(List.of(responsible.getId())))
          .thenReturn(List.of(responsible));
      when(activityAuthorizationRepository.saveAll(anyList()))
          .thenAnswer(invocation -> invocation.getArgument(0));

      var result =
          service.defineActivityAuthorizations(
              activityId,
              List.of(
                  responsibleDefinition(responsible),
                  new ActivityAuthorizationDefinition(reviewer.getId(), Set.of()),
                  new ActivityAuthorizationDefinition(null, EActivityRight.all())));

      assertThat(result).hasSize(1);
      assertThat(result.getFirst().getStaff()).isEqualTo(responsible);
    }

    @Test
    void thenItShouldThrowWhenNoResponsibleHoldsEveryManagementRight() {
      BddLogger.then("an ActivityResponsibleRequiredException should be thrown");
      givenManager();

      assertThatThrownBy(
              () ->
                  service.defineActivityAuthorizations(
                      activityId,
                      List.of(
                          new ActivityAuthorizationDefinition(
                              responsible.getId(),
                              EnumSet.of(
                                  EActivityRight.RESPONSIBLE,
                                  EActivityRight.EDIT,
                                  EActivityRight.PUBLISH)),
                          new ActivityAuthorizationDefinition(
                              reviewer.getId(), EnumSet.of(EActivityRight.MANAGE_AUTHORIZATIONS)))))
          .isInstanceOf(ActivityResponsibleRequiredException.class);
      verify(activityAuthorizationRepository, never()).saveAll(anyList());
      verify(activityAuthorizationRepository, never()).removeAllFromDatabase(anyList());
    }

    @Test
    void thenItShouldThrowWhenTheDefinitionsAreEmpty() {
      BddLogger.then("an ActivityResponsibleRequiredException should be thrown");
      givenManager();

      assertThatThrownBy(() -> service.defineActivityAuthorizations(activityId, List.of()))
          .isInstanceOf(ActivityResponsibleRequiredException.class);
      assertThatThrownBy(() -> service.defineActivityAuthorizations(activityId, null))
          .isInstanceOf(ActivityResponsibleRequiredException.class);
    }

    @Test
    void thenItShouldThrowWhenAStaffDoesNotExist() {
      BddLogger.then("a UserIsNotStaffException should be thrown");
      givenManager();
      when(staffRepository.findAllById(anyList())).thenReturn(List.of(responsible));

      assertThatThrownBy(
              () ->
                  service.defineActivityAuthorizations(
                      activityId,
                      List.of(
                          responsibleDefinition(responsible),
                          new ActivityAuthorizationDefinition(
                              reviewer.getId(), EnumSet.of(EActivityRight.FEEDBACK)))))
          .isInstanceOf(UserIsNotStaffException.class);
      verify(activityAuthorizationRepository, never()).saveAll(anyList());
    }

    @Test
    void thenItShouldThrowWhenTheStaffCannotManageTheAuthorizations() {
      BddLogger.then("a UserNotAuthorizedException should be thrown");
      givenAuthorization(authorizationOf(staff, EActivityRight.EDIT));

      assertThatThrownBy(
              () ->
                  service.defineActivityAuthorizations(
                      activityId, List.of(responsibleDefinition(responsible))))
          .isInstanceOf(UserNotAuthorizedException.class);
    }

    @Test
    void thenItShouldThrowWhenTheActivityDoesNotExist() {
      BddLogger.then("an ActivityNotFoundException should be thrown");
      when(activityRepository.existsById(activityId)).thenReturn(false);
      when(activityDraftRepository.existsById(activityId)).thenReturn(false);

      assertThatThrownBy(
              () ->
                  service.defineActivityAuthorizations(
                      activityId, List.of(responsibleDefinition(responsible))))
          .isInstanceOf(ActivityNotFoundException.class);
    }
  }

  @Nested
  class WhenGrantingAllRights {

    @BeforeEach
    void setupWhen() {
      BddLogger.given("an activity authorization service");
      BddLogger.when("granting every right to a staff");
    }

    @Test
    void thenItShouldCreateAnAuthorizationHoldingEveryRight() {
      BddLogger.then("a new authorization with every right should be saved");
      givenNoAuthorization(staff);

      service.grantAllRights(activityId, staff);

      ArgumentCaptor<ActivityAuthorization> captor =
          ArgumentCaptor.forClass(ActivityAuthorization.class);
      verify(activityAuthorizationRepository).save(captor.capture());
      assertThat(captor.getValue().getActivityId()).isEqualTo(activityId);
      assertThat(captor.getValue().getStaff()).isEqualTo(staff);
      assertThat(captor.getValue().getRights())
          .containsExactlyInAnyOrderElementsOf(EActivityRight.all());
    }

    @Test
    void thenItShouldCompleteAnExistingAuthorization() {
      BddLogger.then("the existing authorization should be completed with every right");
      var existing = authorizationOf(staff, EActivityRight.FEEDBACK);
      givenAuthorization(existing);

      service.grantAllRights(activityId, staff);

      verify(activityAuthorizationRepository).save(existing);
      assertThat(existing.getRights()).containsExactlyInAnyOrderElementsOf(EActivityRight.all());
    }
  }

  @Nested
  class WhenListingStaffs {

    @BeforeEach
    void setupWhen() {
      BddLogger.given("an activity authorization service");
      BddLogger.when("listing the staffs by right");
    }

    @Test
    void thenItShouldReturnTheStaffsHoldingTheRight() {
      BddLogger.then("only the staffs holding the right should be returned");
      var reviewer = StaffFixture.create().toModel();
      when(activityAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(
              List.of(
                  authorizationOf(staff, EActivityRight.RESPONSIBLE, EActivityRight.FEEDBACK),
                  authorizationOf(reviewer, EActivityRight.FEEDBACK)));

      assertThat(service.getStaffsWithRight(activityId, EActivityRight.FEEDBACK))
          .containsExactly(staff, reviewer);
      assertThat(service.getStaffsWithRight(activityId, EActivityRight.RESPONSIBLE))
          .containsExactly(staff);
    }

    @Test
    void thenItShouldGroupTheResponsiblesByActivity() {
      BddLogger.then("the responsibles should be grouped by activity id");
      var otherActivityId = UUID.randomUUID();
      var reviewer = StaffFixture.create().toModel();
      var ids = List.of(activityId, otherActivityId);
      when(activityAuthorizationRepository.findAllByActivityIds(ids))
          .thenReturn(
              List.of(
                  authorizationOf(staff, EActivityRight.RESPONSIBLE),
                  authorizationOf(reviewer, EActivityRight.FEEDBACK),
                  ActivityAuthorization.create(
                      otherActivityId, reviewer, EnumSet.of(EActivityRight.RESPONSIBLE))));

      var result = service.getResponsiblesByActivityIds(ids);

      assertThat(result.get(activityId)).containsExactly(staff);
      assertThat(result.get(otherActivityId)).containsExactly(reviewer);
    }

    @Test
    void thenItShouldNotQueryTheRepositoryWithoutActivityIds() {
      BddLogger.then("an empty map should be returned");

      assertThat(service.getResponsiblesByActivityIds(List.of())).isEmpty();
      verify(activityAuthorizationRepository, never()).findAllByActivityIds(any());
    }
  }

  @Test
  void shouldDeleteEveryAuthorizationOfTheActivity() {
    BddLogger.given("an activity authorization service");
    BddLogger.when("deleting the authorizations of an activity");
    BddLogger.then("the repository should delete them all");

    service.deleteAllOfActivity(activityId);

    verify(activityAuthorizationRepository).deleteAllByActivityId(activityId);
  }
}
