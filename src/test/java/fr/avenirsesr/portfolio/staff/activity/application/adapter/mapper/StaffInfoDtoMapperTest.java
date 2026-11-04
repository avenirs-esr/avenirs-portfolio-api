package fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.StaffCandidateDTO;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class StaffInfoDtoMapperTest {

  private final StaffInfoDtoMapper mapper = Mappers.getMapper(StaffInfoDtoMapper.class);

  @Test
  void shouldMapStaffToCandidateDTO() {
    BddLogger.given("a staff with an institution email");
    var user = UserFixture.create().withFirstName("Jeanne").withLastName("Dupont").toModel();
    var staff = Staff.create(user, "jeanne.dupont@university.com", "bio");

    BddLogger.when("mapping to StaffCandidateDTO");
    var dto = mapper.toCandidateDTO(staff);

    BddLogger.then("the identity and the institution email should be exposed");
    assertThat(dto)
        .isEqualTo(
            new StaffCandidateDTO(
                staff.getId(), "Jeanne", "Dupont", "jeanne.dupont@university.com"));
  }

  @Test
  void shouldMapNullStaffToNullCandidateDTO() {
    BddLogger.given("no staff");
    BddLogger.when("mapping to StaffCandidateDTO");
    BddLogger.then("it should return null");
    assertThat(mapper.toCandidateDTO(null)).isNull();
  }
}
