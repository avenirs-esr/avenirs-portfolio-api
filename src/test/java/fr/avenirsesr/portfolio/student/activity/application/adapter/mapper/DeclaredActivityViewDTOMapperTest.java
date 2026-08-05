package fr.avenirsesr.portfolio.student.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.DeclaredActivityViewDTO;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredActivityViewDTOMapperTest {

  @Spy private OptionalMapper optionalMapper = Mappers.getMapper(OptionalMapper.class);
  @InjectMocks private DeclaredActivityViewDTOMapperImpl mapper;

  @Test
  void shouldMapDeclaredActivityToViewDTO() {
    BddLogger.given("a declared activity");

    Student student = StudentFixture.create().toModel();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(),
            student,
            activity,
            null,
            "My reflection",
            LocalDate.now(),
            LocalDate.now().plusMonths(3),
            null);

    BddLogger.when("mapping to DeclaredActivityViewDTO");

    DeclaredActivityViewDTO dto =
        mapper.toDTO(declaredActivity, EDeclaredActivityStatus.SUBSCRIBED);

    BddLogger.then("it should map nested activity fields to top-level DTO fields");

    assertNotNull(dto);
    assertEquals(activity.getId(), dto.activityId());
    assertEquals(activity.getTitle(), dto.title());
    assertEquals(activity.getThematic(), dto.thematic());
    assertEquals(activity.getSummary(), dto.summary());
    assertEquals(activity.getDescription(), dto.description());
    assertEquals(EDeclaredActivityStatus.SUBSCRIBED, dto.status());
    assertFalse(dto.valorized());
  }
}
