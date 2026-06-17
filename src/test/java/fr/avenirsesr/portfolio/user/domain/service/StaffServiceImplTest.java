package fr.avenirsesr.portfolio.user.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StaffServiceImplTest {

  @Mock private StaffRepository staffRepository;
  @Mock private UserRepository userRepository;
  @Mock private LoggedInUserService loggedInUserService;

  private StaffServiceImpl staffService;

  @BeforeEach
  void setUp() {
    staffService = new StaffServiceImpl(staffRepository, userRepository, loggedInUserService);
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
}
