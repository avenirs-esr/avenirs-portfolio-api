package fr.avenirsesr.portfolio.user.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StaffFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StaffServiceImplTest {

  @Mock private StaffRepository staffRepository;
  @Mock private UserRepository userRepository;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private FileResourceService fileResourceService;

  private StaffServiceImpl staffService;

  @BeforeEach
  void setUp() {
    staffService =
        new StaffServiceImpl(
            staffRepository, userRepository, loggedInUserService, fileResourceService);
  }

  @Nested
  class GetStaffProfile {

    @Test
    void shouldMapStaffFieldsToProfileOverviewData() {
      BddLogger.given("a logged-in staff with known field values");
      var user =
          UserFixture.create()
              .withFirstName("Marie")
              .withLastName("Dupont")
              .withEmail("marie@university.com")
              .toModel();
      Staff staff =
          Staff.toDomain(
              user,
              "marie@university.com",
              null,
              null,
              "My staff bio",
              false,
              null,
              null,
              Instant.now(),
              Instant.now());
      when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);

      BddLogger.when("getting the staff profile");
      var result = staffService.getStaffProfile();

      BddLogger.then("all fields should be correctly mapped");
      assertEquals(staff.getId(), result.id());
      assertEquals("Marie", result.firstName());
      assertEquals("Dupont", result.lastName());
      assertEquals("marie@university.com", result.email());
      assertEquals("My staff bio", result.bio());
    }
  }

  @Nested
  class UpdateAffiliations {

    @Test
    void shouldReplaceInstitutionAndGroupIdsAndSaveTheStaff() {
      BddLogger.given("an existing staff and new affiliation ids from the back-office");
      Staff staff = StaffFixture.create().toModel();
      UUID institutionId = UUID.randomUUID();
      UUID groupId = UUID.randomUUID();
      when(staffRepository.findById(staff.getId())).thenReturn(Optional.of(staff));

      BddLogger.when("updating the staff's affiliations");
      staffService.updateAffiliations(staff.getId(), List.of(institutionId), List.of(groupId));

      BddLogger.then("the staff's institution and group ids are replaced and saved");
      ArgumentCaptor<Staff> staffCaptor = ArgumentCaptor.forClass(Staff.class);
      verify(staffRepository).save(staffCaptor.capture());
      assertEquals(List.of(institutionId), staffCaptor.getValue().getInstitutionIds());
      assertEquals(List.of(groupId), staffCaptor.getValue().getGroupIds());
    }
  }
}
