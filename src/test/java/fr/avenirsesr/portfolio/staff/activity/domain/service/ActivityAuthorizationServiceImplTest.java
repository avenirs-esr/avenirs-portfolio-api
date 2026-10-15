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
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityDraftNotFoundException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityLeaderRequiredException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityAuthorizationRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftAuthorizationRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStaffException;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StaffFixture;
import java.util.Arrays;
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
  @Mock private ActivityDraftAuthorizationRepository activityDraftAuthorizationRepository;
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

  private void givenActivityAuthorization(ActivityAuthorization authorization) {
    when(activityAuthorizationRepository.findByActivityIdAndStaffId(
            activityId, authorization.getStaff().getId()))
        .thenReturn(Optional.of(authorization));
  }

  private void givenDraftAuthorization(ActivityAuthorization authorization) {
    when(activityDraftAuthorizationRepository.findByActivityIdAndStaffId(
            activityId, authorization.getStaff().getId()))
        .thenReturn(Optional.of(authorization));
  }

  private void givenNoActivityAuthorization(Staff holder) {
    when(activityAuthorizationRepository.findByActivityIdAndStaffId(activityId, holder.getId()))
        .thenReturn(Optional.empty());
  }

  private void givenNoDraftAuthorization(Staff holder) {
    when(activityDraftAuthorizationRepository.findByActivityIdAndStaffId(
            activityId, holder.getId()))
        .thenReturn(Optional.empty());
  }

  @Nested
  class WhenCheckingARightOnAnActivity {

    @BeforeEach
    void setupWhen() {
      BddLogger.given("an activity authorization service");
      BddLogger.when("checking whether a staff holds a right on a published activity");
    }

    @Test
    void thenItShouldBeGrantedWhenTheAuthorizationHoldsTheRight() {
      BddLogger.then("the right should be granted");
      givenActivityAuthorization(authorizationOf(staff, EActivityRight.EDIT));

      assertThat(service.hasRight(staff, activityId, EActivityRight.EDIT)).isTrue();
      assertThat(service.hasRight(staff, activityId, EActivityRight.FEEDBACK)).isFalse();
    }

    @Test
    void thenItShouldBeDeniedWhenTheStaffHasNoAuthorization() {
      BddLogger.then("the right should be denied");
      givenNoActivityAuthorization(staff);

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
    void thenItShouldNotReadTheDraftAuthorizations() {
      BddLogger.then("a right held on the draft should not apply to the activity");
      givenNoActivityAuthorization(staff);

      assertThat(service.hasRight(staff, activityId, EActivityRight.EDIT)).isFalse();
      verify(activityDraftAuthorizationRepository, never())
          .findByActivityIdAndStaffId(any(), any());
    }

    @Test
    void thenAnyOfTheRightsShouldBeEnough() {
      BddLogger.then("holding one of the candidate rights should be enough");
      givenActivityAuthorization(authorizationOf(staff, EActivityRight.FEEDBACK));

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
      givenActivityAuthorization(authorizationOf(staff, EActivityRight.FEEDBACK));

      assertThatThrownBy(() -> service.checkRight(staff, activityId, EActivityRight.EDIT))
          .isInstanceOf(UserNotAuthorizedException.class);
      assertThatThrownBy(
              () -> service.checkAnyRight(staff, activityId, EActivityRight.MANAGEMENT_RIGHTS))
          .isInstanceOf(UserNotAuthorizedException.class);
    }

    @Test
    void thenCheckRightShouldPassWhenTheRightIsHeld() {
      BddLogger.then("no exception should be thrown");
      givenActivityAuthorization(authorizationOf(staff, EActivityRight.FEEDBACK));

      service.checkRight(staff, activityId, EActivityRight.FEEDBACK);
      service.checkAnyRight(staff, activityId, EnumSet.of(EActivityRight.FEEDBACK));
    }

    @Test
    void thenGetRightsShouldReturnTheHeldRights() {
      BddLogger.then("the held rights should be returned");
      givenActivityAuthorization(
          authorizationOf(staff, EActivityRight.EDIT, EActivityRight.PUBLISH));

      assertThat(service.getRights(staff, activityId))
          .containsExactlyInAnyOrder(EActivityRight.EDIT, EActivityRight.PUBLISH);
    }

    @Test
    void thenTheLoggedInStaffRightsShouldBeEmptyWhenTheUserIsNotAStaff() {
      BddLogger.then("no right should be returned to a user who is not a staff");
      when(loggedInUserService.getLoggedInStaff()).thenThrow(new UserIsNotStaffException());

      assertThat(service.getLoggedInStaffRights(activityId)).isEmpty();
      assertThat(service.getLoggedInStaffDraftRights(activityId)).isEmpty();
    }

    @Test
    void thenTheLoggedInStaffRightsShouldComeFromTheActivity() {
      BddLogger.then("the rights of the logged-in staff should be read on the activity");
      when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
      givenActivityAuthorization(authorizationOf(staff, EActivityRight.EDIT));

      assertThat(service.getLoggedInStaffRights(activityId)).containsExactly(EActivityRight.EDIT);
    }
  }

  @Nested
  class WhenCheckingARightOnADraft {

    @BeforeEach
    void setupWhen() {
      BddLogger.given("an activity authorization service");
      BddLogger.when("checking whether a staff holds a right on a draft");
    }

    @Test
    void thenItShouldReadTheDraftAuthorizationsOnly() {
      BddLogger.then("the draft authorizations should answer, not the activity ones");
      givenDraftAuthorization(authorizationOf(staff, EActivityRight.EDIT));

      assertThat(service.hasDraftRight(staff, activityId, EActivityRight.EDIT)).isTrue();
      assertThat(service.hasDraftRight(staff, activityId, EActivityRight.PUBLISH)).isFalse();
      verify(activityAuthorizationRepository, never()).findByActivityIdAndStaffId(any(), any());
    }

    @Test
    void thenAnyOfTheRightsShouldBeEnough() {
      BddLogger.then("holding one of the candidate rights should be enough");
      givenDraftAuthorization(authorizationOf(staff, EActivityRight.EDIT));

      assertThat(service.hasAnyDraftRight(staff, activityId, EActivityRight.MANAGEMENT_RIGHTS))
          .isTrue();
      assertThat(service.hasAnyDraftRight(staff, activityId, EnumSet.of(EActivityRight.FEEDBACK)))
          .isFalse();
    }

    @Test
    void thenCheckDraftRightShouldThrowWhenTheRightIsMissing() {
      BddLogger.then("a UserNotAuthorizedException should be thrown");
      givenNoDraftAuthorization(staff);

      assertThatThrownBy(() -> service.checkDraftRight(staff, activityId, EActivityRight.EDIT))
          .isInstanceOf(UserNotAuthorizedException.class);
      assertThatThrownBy(
              () -> service.checkAnyDraftRight(staff, activityId, EActivityRight.MANAGEMENT_RIGHTS))
          .isInstanceOf(UserNotAuthorizedException.class);
    }

    @Test
    void thenCheckDraftRightShouldPassWhenTheRightIsHeld() {
      BddLogger.then("no exception should be thrown");
      givenDraftAuthorization(authorizationOf(staff, EActivityRight.EDIT));

      service.checkDraftRight(staff, activityId, EActivityRight.EDIT);
      service.checkAnyDraftRight(staff, activityId, EActivityRight.MANAGEMENT_RIGHTS);
    }

    @Test
    void thenTheLoggedInStaffDraftRightsShouldComeFromTheDraft() {
      BddLogger.then("the rights of the logged-in staff should be read on the draft");
      when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
      givenDraftAuthorization(authorizationOf(staff, EActivityRight.FEEDBACK));

      assertThat(service.getLoggedInStaffDraftRights(activityId))
          .containsExactly(EActivityRight.FEEDBACK);
    }
  }

  @Nested
  class WhenGettingTheAuthorizations {

    @BeforeEach
    void setupWhen() {
      BddLogger.given("an activity authorization service");
      BddLogger.when("getting the authorizations of an activity or of its draft");
      lenient().when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    }

    @Test
    void thenTheActivityAuthorizationsShouldBeReturnedToAStaffHoldingAnyRight() {
      BddLogger.then("the authorizations should be returned");
      var other = StaffFixture.create().toModel();
      var mine = authorizationOf(staff, EActivityRight.FEEDBACK);
      var theirs = authorizationOf(other, EActivityRight.LEADER);
      when(activityRepository.existsById(activityId)).thenReturn(true);
      givenActivityAuthorization(mine);
      when(activityAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(List.of(mine, theirs));

      assertThat(service.getActivityAuthorizations(activityId)).containsExactly(mine, theirs);
    }

    @Test
    void thenTheDraftAuthorizationsShouldBeReturnedToAStaffHoldingAnyRight() {
      BddLogger.then("the draft authorizations should be returned");
      var mine = authorizationOf(staff, EActivityRight.EDIT);
      when(activityDraftRepository.existsById(activityId)).thenReturn(true);
      givenDraftAuthorization(mine);
      when(activityDraftAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(List.of(mine));

      assertThat(service.getDraftAuthorizations(activityId)).containsExactly(mine);
      verify(activityRepository, never()).existsById(any());
    }

    @Test
    void thenItShouldThrowWhenTheActivityDoesNotExist() {
      BddLogger.then("an ActivityNotFoundException should be thrown");
      when(activityRepository.existsById(activityId)).thenReturn(false);

      assertThatThrownBy(() -> service.getActivityAuthorizations(activityId))
          .isInstanceOf(ActivityNotFoundException.class);
    }

    @Test
    void thenItShouldThrowWhenTheDraftDoesNotExist() {
      BddLogger.then("an ActivityDraftNotFoundException should be thrown");
      when(activityDraftRepository.existsById(activityId)).thenReturn(false);

      assertThatThrownBy(() -> service.getDraftAuthorizations(activityId))
          .isInstanceOf(ActivityDraftNotFoundException.class);
    }

    @Test
    void thenItShouldThrowWhenTheStaffHoldsNoRight() {
      BddLogger.then("a UserNotAuthorizedException should be thrown");
      when(activityRepository.existsById(activityId)).thenReturn(true);
      givenNoActivityAuthorization(staff);

      assertThatThrownBy(() -> service.getActivityAuthorizations(activityId))
          .isInstanceOf(UserNotAuthorizedException.class);
    }
  }

  @Nested
  class WhenDefiningTheAuthorizations {

    Staff leader;
    Staff reviewer;

    @BeforeEach
    void setupWhen() {
      BddLogger.given("an activity authorization service");
      BddLogger.when("defining the authorizations of an activity or of its draft");
      leader = StaffFixture.create().toModel();
      reviewer = StaffFixture.create().toModel();
      lenient().when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
      lenient().when(activityRepository.existsById(activityId)).thenReturn(true);
      lenient().when(activityDraftRepository.existsById(activityId)).thenReturn(true);
    }

    private ActivityAuthorizationDefinition leaderDefinition(Staff holder) {
      return new ActivityAuthorizationDefinition(holder.getId(), EActivityRight.all());
    }

    @Test
    void thenItShouldCreateUpdateAndRemoveActivityAuthorizations() {
      BddLogger.then("new entries are created, kept ones updated and missing ones removed");
      givenActivityAuthorization(authorizationOf(staff, EActivityRight.MANAGE_AUTHORIZATIONS));
      var removedStaff = StaffFixture.create().toModel();
      var existingReviewer = authorizationOf(reviewer, EActivityRight.FEEDBACK);
      var removed = authorizationOf(removedStaff, EActivityRight.EVALUATION);
      when(activityAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(List.of(existingReviewer, removed));
      when(staffRepository.findAllById(anyList())).thenReturn(List.of(leader, reviewer));
      when(activityAuthorizationRepository.saveAll(anyList()))
          .thenAnswer(invocation -> invocation.getArgument(0));

      var result =
          service.defineActivityAuthorizations(
              activityId,
              List.of(
                  leaderDefinition(leader),
                  new ActivityAuthorizationDefinition(
                      reviewer.getId(),
                      EnumSet.of(EActivityRight.FEEDBACK, EActivityRight.EVALUATION))));

      verify(activityAuthorizationRepository).removeAllFromDatabase(List.of(removed));
      ArgumentCaptor<List<ActivityAuthorization>> captor = ArgumentCaptor.captor();
      verify(activityAuthorizationRepository).saveAll(captor.capture());
      var saved = captor.getValue();
      assertThat(saved).hasSize(2);
      assertThat(saved.getFirst().getStaff()).isEqualTo(leader);
      assertThat(saved.getFirst().getRights())
          .containsExactlyInAnyOrderElementsOf(EActivityRight.all());
      assertThat(saved.get(1)).isSameAs(existingReviewer);
      assertThat(existingReviewer.getRights())
          .containsExactlyInAnyOrder(EActivityRight.FEEDBACK, EActivityRight.EVALUATION);
      assertThat(result).isEqualTo(saved);
      verify(activityDraftAuthorizationRepository, never()).saveAll(anyList());
    }

    @Test
    void thenTheDraftAuthorizationsShouldBeWrittenInTheirOwnTable() {
      BddLogger.then("defining the draft authorizations should not touch the activity ones");
      givenDraftAuthorization(authorizationOf(staff, EActivityRight.MANAGE_AUTHORIZATIONS));
      when(activityDraftAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(List.of());
      when(staffRepository.findAllById(anyList())).thenReturn(List.of(leader));
      when(activityDraftAuthorizationRepository.saveAll(anyList()))
          .thenAnswer(invocation -> invocation.getArgument(0));

      var result = service.defineDraftAuthorizations(activityId, List.of(leaderDefinition(leader)));

      assertThat(result).hasSize(1);
      assertThat(result.getFirst().getStaff()).isEqualTo(leader);
      verify(activityAuthorizationRepository, never()).findAllByActivityId(any());
      verify(activityAuthorizationRepository, never()).saveAll(anyList());
    }

    @Test
    void thenItShouldMergeDuplicatedStaffEntries() {
      BddLogger.then("the rights of a staff listed twice should be merged");
      givenActivityAuthorization(authorizationOf(staff, EActivityRight.MANAGE_AUTHORIZATIONS));
      when(activityAuthorizationRepository.findAllByActivityId(activityId)).thenReturn(List.of());
      when(staffRepository.findAllById(anyList())).thenReturn(List.of(leader));
      when(activityAuthorizationRepository.saveAll(anyList()))
          .thenAnswer(invocation -> invocation.getArgument(0));

      var result =
          service.defineActivityAuthorizations(
              activityId,
              List.of(
                  new ActivityAuthorizationDefinition(
                      leader.getId(), EActivityRight.MANAGEMENT_RIGHTS),
                  new ActivityAuthorizationDefinition(
                      leader.getId(), EnumSet.of(EActivityRight.FEEDBACK))));

      assertThat(result).hasSize(1);
      assertThat(result.getFirst().getRights())
          .containsExactlyInAnyOrder(
              EActivityRight.LEADER,
              EActivityRight.EDIT,
              EActivityRight.MANAGE_AUTHORIZATIONS,
              EActivityRight.PUBLISH,
              EActivityRight.FEEDBACK);
    }

    @Test
    void thenItShouldIgnoreEntriesWithoutRights() {
      BddLogger.then("an entry without rights should not create an authorization");
      givenActivityAuthorization(authorizationOf(staff, EActivityRight.MANAGE_AUTHORIZATIONS));
      when(activityAuthorizationRepository.findAllByActivityId(activityId)).thenReturn(List.of());
      when(staffRepository.findAllById(List.of(leader.getId()))).thenReturn(List.of(leader));
      when(activityAuthorizationRepository.saveAll(anyList()))
          .thenAnswer(invocation -> invocation.getArgument(0));

      var result =
          service.defineActivityAuthorizations(
              activityId,
              Arrays.asList(
                  leaderDefinition(leader),
                  new ActivityAuthorizationDefinition(reviewer.getId(), Set.of()),
                  new ActivityAuthorizationDefinition(null, EActivityRight.all())));

      assertThat(result).hasSize(1);
      assertThat(result.getFirst().getStaff()).isEqualTo(leader);
    }

    @Test
    void thenItShouldThrowWhenNoLeaderHoldsEveryManagementRight() {
      BddLogger.then("an ActivityLeaderRequiredException should be thrown");
      givenActivityAuthorization(authorizationOf(staff, EActivityRight.MANAGE_AUTHORIZATIONS));

      assertThatThrownBy(
              () ->
                  service.defineActivityAuthorizations(
                      activityId,
                      List.of(
                          new ActivityAuthorizationDefinition(
                              leader.getId(),
                              EnumSet.of(
                                  EActivityRight.LEADER,
                                  EActivityRight.EDIT,
                                  EActivityRight.PUBLISH)),
                          new ActivityAuthorizationDefinition(
                              reviewer.getId(), EnumSet.of(EActivityRight.MANAGE_AUTHORIZATIONS)))))
          .isInstanceOf(ActivityLeaderRequiredException.class);
      verify(activityAuthorizationRepository, never()).saveAll(anyList());
      verify(activityAuthorizationRepository, never()).removeAllFromDatabase(anyList());
    }

    @Test
    void thenTheDraftShouldAlsoRequireALeader() {
      BddLogger.then("an ActivityLeaderRequiredException should be thrown");
      givenDraftAuthorization(authorizationOf(staff, EActivityRight.MANAGE_AUTHORIZATIONS));

      assertThatThrownBy(
              () ->
                  service.defineDraftAuthorizations(
                      activityId,
                      List.of(
                          new ActivityAuthorizationDefinition(
                              leader.getId(), EnumSet.of(EActivityRight.FEEDBACK)))))
          .isInstanceOf(ActivityLeaderRequiredException.class);
      verify(activityDraftAuthorizationRepository, never()).saveAll(anyList());
    }

    @Test
    void thenItShouldThrowWhenTheDefinitionsAreEmpty() {
      BddLogger.then("an ActivityLeaderRequiredException should be thrown");
      givenActivityAuthorization(authorizationOf(staff, EActivityRight.MANAGE_AUTHORIZATIONS));

      assertThatThrownBy(() -> service.defineActivityAuthorizations(activityId, List.of()))
          .isInstanceOf(ActivityLeaderRequiredException.class);
      assertThatThrownBy(() -> service.defineActivityAuthorizations(activityId, null))
          .isInstanceOf(ActivityLeaderRequiredException.class);
    }

    @Test
    void thenItShouldThrowWhenAStaffDoesNotExist() {
      BddLogger.then("a UserIsNotStaffException should be thrown");
      givenActivityAuthorization(authorizationOf(staff, EActivityRight.MANAGE_AUTHORIZATIONS));
      when(staffRepository.findAllById(anyList())).thenReturn(List.of(leader));

      assertThatThrownBy(
              () ->
                  service.defineActivityAuthorizations(
                      activityId,
                      List.of(
                          leaderDefinition(leader),
                          new ActivityAuthorizationDefinition(
                              reviewer.getId(), EnumSet.of(EActivityRight.FEEDBACK)))))
          .isInstanceOf(UserIsNotStaffException.class);
      verify(activityAuthorizationRepository, never()).saveAll(anyList());
    }

    @Test
    void thenItShouldThrowWhenTheStaffCannotManageTheAuthorizations() {
      BddLogger.then("a UserNotAuthorizedException should be thrown");
      givenActivityAuthorization(authorizationOf(staff, EActivityRight.EDIT));

      assertThatThrownBy(
              () ->
                  service.defineActivityAuthorizations(
                      activityId, List.of(leaderDefinition(leader))))
          .isInstanceOf(UserNotAuthorizedException.class);
    }

    @Test
    void thenItShouldThrowWhenTheStaffCannotManageTheDraftAuthorizations() {
      BddLogger.then("a UserNotAuthorizedException should be thrown");
      givenDraftAuthorization(authorizationOf(staff, EActivityRight.EDIT));

      assertThatThrownBy(
              () ->
                  service.defineDraftAuthorizations(activityId, List.of(leaderDefinition(leader))))
          .isInstanceOf(UserNotAuthorizedException.class);
    }

    @Test
    void thenItShouldThrowWhenTheDraftDoesNotExist() {
      BddLogger.then("an ActivityDraftNotFoundException should be thrown");
      when(activityDraftRepository.existsById(activityId)).thenReturn(false);

      assertThatThrownBy(
              () ->
                  service.defineDraftAuthorizations(activityId, List.of(leaderDefinition(leader))))
          .isInstanceOf(ActivityDraftNotFoundException.class);
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
    void thenItShouldCreateAnActivityAuthorizationHoldingEveryRight() {
      BddLogger.then("a new activity authorization with every right should be saved");
      givenNoActivityAuthorization(staff);

      service.grantAllRights(activityId, staff);

      ArgumentCaptor<ActivityAuthorization> captor =
          ArgumentCaptor.forClass(ActivityAuthorization.class);
      verify(activityAuthorizationRepository).save(captor.capture());
      assertThat(captor.getValue().getActivityId()).isEqualTo(activityId);
      assertThat(captor.getValue().getStaff()).isEqualTo(staff);
      assertThat(captor.getValue().getRights())
          .containsExactlyInAnyOrderElementsOf(EActivityRight.all());
      verify(activityDraftAuthorizationRepository, never()).save(any());
    }

    @Test
    void thenItShouldCreateADraftAuthorizationHoldingEveryRight() {
      BddLogger.then("a new draft authorization with every right should be saved");
      givenNoDraftAuthorization(staff);

      service.grantAllDraftRights(activityId, staff);

      ArgumentCaptor<ActivityAuthorization> captor =
          ArgumentCaptor.forClass(ActivityAuthorization.class);
      verify(activityDraftAuthorizationRepository).save(captor.capture());
      assertThat(captor.getValue().getRights())
          .containsExactlyInAnyOrderElementsOf(EActivityRight.all());
      verify(activityAuthorizationRepository, never()).save(any());
    }

    @Test
    void thenItShouldCompleteAnExistingAuthorization() {
      BddLogger.then("the existing authorization should be completed with every right");
      var existing = authorizationOf(staff, EActivityRight.FEEDBACK);
      givenActivityAuthorization(existing);

      service.grantAllRights(activityId, staff);

      verify(activityAuthorizationRepository).save(existing);
      assertThat(existing.getRights()).containsExactlyInAnyOrderElementsOf(EActivityRight.all());
    }
  }

  @Nested
  class WhenCopyingTheActivityAuthorizationsToTheDraft {

    Staff reviewer;

    @BeforeEach
    void setupWhen() {
      BddLogger.given("an activity authorization service");
      BddLogger.when("creating the edition draft of an activity");
      reviewer = StaffFixture.create().toModel();
    }

    @Test
    void thenTheDraftShouldStartFromTheActivityAuthorizations() {
      BddLogger.then("each activity authorization should be copied to the draft table");
      when(activityAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(
              List.of(
                  authorizationOf(staff, EActivityRight.values()),
                  authorizationOf(reviewer, EActivityRight.FEEDBACK)));
      when(activityDraftAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(List.of());
      when(activityDraftAuthorizationRepository.saveAll(anyList()))
          .thenAnswer(invocation -> invocation.getArgument(0));

      service.copyActivityAuthorizationsToDraft(activityId);

      ArgumentCaptor<List<ActivityAuthorization>> captor = ArgumentCaptor.captor();
      verify(activityDraftAuthorizationRepository).saveAll(captor.capture());
      assertThat(captor.getValue()).hasSize(2);
      assertThat(captor.getValue().getFirst().getRights())
          .containsExactlyInAnyOrderElementsOf(EActivityRight.all());
      assertThat(captor.getValue().get(1).getStaff()).isEqualTo(reviewer);
      assertThat(captor.getValue().get(1).getRights()).containsExactly(EActivityRight.FEEDBACK);
      verify(activityAuthorizationRepository, never()).saveAll(anyList());
    }

    @Test
    void thenAnExistingDraftAuthorizationShouldBeAligned() {
      BddLogger.then("the draft rows should be aligned with the activity ones");
      var staleDraft = authorizationOf(staff, EActivityRight.FEEDBACK);
      var removedDraft = authorizationOf(reviewer, EActivityRight.EDIT);
      when(activityAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(List.of(authorizationOf(staff, EActivityRight.PUBLISH)));
      when(activityDraftAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(List.of(staleDraft, removedDraft));
      when(activityDraftAuthorizationRepository.saveAll(anyList()))
          .thenAnswer(invocation -> invocation.getArgument(0));

      service.copyActivityAuthorizationsToDraft(activityId);

      verify(activityDraftAuthorizationRepository).removeAllFromDatabase(List.of(removedDraft));
      assertThat(staleDraft.getRights()).containsExactly(EActivityRight.PUBLISH);
    }

    @Test
    void thenItShouldDoNothingWhenTheActivityHasNoAuthorization() {
      BddLogger.then("nothing should be written in the draft table");
      when(activityAuthorizationRepository.findAllByActivityId(activityId)).thenReturn(List.of());

      service.copyActivityAuthorizationsToDraft(activityId);

      verify(activityDraftAuthorizationRepository, never()).saveAll(anyList());
      verify(activityDraftAuthorizationRepository, never()).removeAllFromDatabase(anyList());
    }
  }

  @Nested
  class WhenApplyingTheDraftAuthorizationsToTheActivity {

    Staff reviewer;

    @BeforeEach
    void setupWhen() {
      BddLogger.given("an activity authorization service");
      BddLogger.when("publishing the draft of an activity");
      reviewer = StaffFixture.create().toModel();
    }

    @Test
    void thenTheActivityShouldTakeTheDraftAuthorizationsAndTheDraftTableShouldBeEmptied() {
      BddLogger.then("the activity rows are aligned with the draft ones, then the draft is wiped");
      var existingActivity = authorizationOf(staff, EActivityRight.FEEDBACK);
      var removedActivity = authorizationOf(reviewer, EActivityRight.EDIT);
      var newcomer = StaffFixture.create().toModel();
      when(activityDraftAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(
              List.of(
                  authorizationOf(staff, EActivityRight.values()),
                  authorizationOf(newcomer, EActivityRight.EVALUATION)));
      when(activityAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(List.of(existingActivity, removedActivity));
      when(activityAuthorizationRepository.saveAll(anyList()))
          .thenAnswer(invocation -> invocation.getArgument(0));

      service.applyDraftAuthorizationsToActivity(activityId);

      verify(activityAuthorizationRepository).removeAllFromDatabase(List.of(removedActivity));
      ArgumentCaptor<List<ActivityAuthorization>> captor = ArgumentCaptor.captor();
      verify(activityAuthorizationRepository).saveAll(captor.capture());
      assertThat(captor.getValue()).hasSize(2);
      assertThat(captor.getValue().getFirst()).isSameAs(existingActivity);
      assertThat(existingActivity.getRights())
          .containsExactlyInAnyOrderElementsOf(EActivityRight.all());
      assertThat(captor.getValue().get(1).getStaff()).isEqualTo(newcomer);
      verify(activityDraftAuthorizationRepository).deleteAllByActivityId(activityId);
    }

    @Test
    void thenItShouldKeepTheActivityAuthorizationsWhenTheDraftHasNone() {
      BddLogger.then("the activity authorizations should be left untouched");
      when(activityDraftAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(List.of());

      service.applyDraftAuthorizationsToActivity(activityId);

      verify(activityAuthorizationRepository, never()).saveAll(anyList());
      verify(activityAuthorizationRepository, never()).removeAllFromDatabase(anyList());
      verify(activityDraftAuthorizationRepository).deleteAllByActivityId(activityId);
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
    void thenItShouldReturnTheStaffsHoldingTheRightOnTheActivity() {
      BddLogger.then("only the staffs holding the right should be returned");
      var reviewer = StaffFixture.create().toModel();
      when(activityAuthorizationRepository.findAllByActivityId(activityId))
          .thenReturn(
              List.of(
                  authorizationOf(staff, EActivityRight.LEADER, EActivityRight.FEEDBACK),
                  authorizationOf(reviewer, EActivityRight.FEEDBACK)));

      assertThat(service.getStaffsWithRight(activityId, EActivityRight.FEEDBACK))
          .containsExactly(staff, reviewer);
      assertThat(service.getStaffsWithRight(activityId, EActivityRight.LEADER))
          .containsExactly(staff);
    }

    @Test
    void thenItShouldGroupTheLeadersByActivity() {
      BddLogger.then("the leaders should be grouped by activity id");
      var otherActivityId = UUID.randomUUID();
      var reviewer = StaffFixture.create().toModel();
      var ids = List.of(activityId, otherActivityId);
      when(activityAuthorizationRepository.findAllByActivityIds(ids))
          .thenReturn(
              List.of(
                  authorizationOf(staff, EActivityRight.LEADER),
                  authorizationOf(reviewer, EActivityRight.FEEDBACK),
                  ActivityAuthorization.create(
                      otherActivityId, reviewer, EnumSet.of(EActivityRight.LEADER))));

      var result = service.getLeadersByActivityIds(ids);

      assertThat(result.get(activityId)).containsExactly(staff);
      assertThat(result.get(otherActivityId)).containsExactly(reviewer);
    }

    @Test
    void thenItShouldGroupTheDraftLeadersFromTheDraftTable() {
      BddLogger.then("the draft leaders should come from the draft table");
      var ids = List.of(activityId);
      when(activityDraftAuthorizationRepository.findAllByActivityIds(ids))
          .thenReturn(List.of(authorizationOf(staff, EActivityRight.LEADER)));

      assertThat(service.getLeadersByDraftIds(ids).get(activityId)).containsExactly(staff);
      verify(activityAuthorizationRepository, never()).findAllByActivityIds(any());
    }

    @Test
    void thenItShouldNotQueryTheRepositoryWithoutActivityIds() {
      BddLogger.then("an empty map should be returned");

      assertThat(service.getLeadersByActivityIds(List.of())).isEmpty();
      assertThat(service.getLeadersByDraftIds(List.of())).isEmpty();
      verify(activityAuthorizationRepository, never()).findAllByActivityIds(any());
      verify(activityDraftAuthorizationRepository, never()).findAllByActivityIds(any());
    }
  }

  @Test
  void shouldDeleteTheAuthorizationsOfTheActivityAndOfItsDraftSeparately() {
    BddLogger.given("an activity authorization service");
    BddLogger.when("deleting the authorizations");
    BddLogger.then("each table should be deleted on its own");

    service.deleteAllOfActivity(activityId);
    verify(activityAuthorizationRepository).deleteAllByActivityId(activityId);
    verify(activityDraftAuthorizationRepository, never()).deleteAllByActivityId(any());

    service.deleteAllOfDraft(activityId);
    verify(activityDraftAuthorizationRepository).deleteAllByActivityId(activityId);
  }
}
