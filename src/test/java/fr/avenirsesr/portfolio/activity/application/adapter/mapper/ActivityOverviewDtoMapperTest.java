package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityOverviewDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.AuthorDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityOverviewDtoMapperTest {

  @Spy private OptionalMapper optionalMapper = Mappers.getMapper(OptionalMapper.class);
  @InjectMocks private ActivityOverviewDtoMapperImpl mapper;

  @Test
  void shouldMapActivityWithStatusToOverviewDTO() {
    BddLogger.given("an activity with student status data");
    UUID userId = UUID.randomUUID();
    User user =
        UserFixture.create()
            .withId(userId)
            .withFirstName("firstname")
            .withLastName("Lastname")
            .toModel();
    var staff = Staff.create(user, "staff@email.com", null, null, "bio");
    Activity activity = ActivityFixture.create().withAuthor(staff).toModel();
    ActivityWithStudentStatusData data =
        new ActivityWithStudentStatusData(activity, true, EDeclaredActivityStatus.SUBSCRIBED);

    BddLogger.when("mapping to ActivityOverviewDTO");
    ActivityOverviewDTO dto = mapper.toDTO(data);

    BddLogger.then("it should map nested activity fields correctly");
    assertNotNull(dto);
    assertEquals(activity.getId(), dto.id());
    assertEquals(new AuthorDTO(staff.getId(), "firstname", "Lastname"), dto.author());
    assertEquals(activity.getTitle(), dto.title());
    assertEquals(activity.getThematic(), dto.thematic());
    assertEquals(activity.getSummary(), dto.summary());
    assertEquals(EDeclaredActivityStatus.SUBSCRIBED, dto.status());
    assertTrue(dto.isNew());
  }
}
