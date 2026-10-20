package fr.avenirsesr.portfolio.user.domain.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.util.List;
import org.junit.jupiter.api.Test;

class EUniverseTest {

  @Test
  void shouldOpenTheStudentUniverseForAStudent() {
    BddLogger.given("the roles of a student");

    BddLogger.then("only the student universe is opened");
    assertThat(EUniverse.of(List.of(ERole.ROLE_STUDENT))).containsExactly(EUniverse.STUDENT);
  }

  @Test
  void shouldOpenTheStaffUniverseForAStaff() {
    BddLogger.given("the roles of a staff");

    BddLogger.then("only the staff universe is opened");
    assertThat(EUniverse.of(List.of(ERole.ROLE_STAFF))).containsExactly(EUniverse.STAFF);
  }

  @Test
  void shouldOpenTheUniverseOfASingleFineGrainedRole() {
    BddLogger.given("a staff holding a single fine-grained staff role");

    BddLogger.then("the staff universe is opened");
    assertThat(EUniverse.of(List.of(ERole.ROLE_STAFF_FEEDBACK_RESPONDER)))
        .containsExactly(EUniverse.STAFF);
    assertThat(EUniverse.of(List.of(ERole.ROLE_STUDENT_PORTFOLIO)))
        .containsExactly(EUniverse.STUDENT);
  }

  @Test
  void shouldOpenBothUniversesWhenTheUserIsStudentAndStaff() {
    BddLogger.given("a user holding both profiles");

    BddLogger.then("both universes are opened");
    assertThat(EUniverse.of(List.of(ERole.ROLE_STUDENT, ERole.ROLE_STAFF)))
        .containsExactlyInAnyOrder(EUniverse.STUDENT, EUniverse.STAFF);
  }

  @Test
  void shouldNotOpenANewUniverseForAdministrationRights() {
    BddLogger.given("a staff with administration rights");

    BddLogger.then("the administration roles add no universe");
    assertThat(EUniverse.of(List.of(ERole.ROLE_STAFF, ERole.ROLE_ADMIN, ERole.ROLE_SUPER_ADMIN)))
        .containsExactly(EUniverse.STAFF);
    assertThat(EUniverse.of(List.of(ERole.ROLE_ADMIN))).isEmpty();
  }

  @Test
  void shouldOpenNoUniverseWithoutRole() {
    BddLogger.given("a user without any role");

    BddLogger.then("no universe is opened");
    assertThat(EUniverse.of(List.of())).isEmpty();
  }
}
