package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityContentDtoMapper;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
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
class DeclaredActivityPresentationDTOMapperTest {

  @Spy
  private ActivityContentDtoMapper activityContentDtoMapper =
      Mappers.getMapper(ActivityContentDtoMapper.class);

  @InjectMocks private DeclaredActivityDetailsDTOMapperImpl mapper;

  @Test
  void shouldMapDeclaredActivityToDetailsDTO() {
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

    BddLogger.when("mapping to DeclaredActivityDetailsDTO");
    DeclaredActivityDetailsDTO dto = mapper.toDTO(declaredActivity);

    BddLogger.then("it should map activity via ActivityDtoMapper and include status");
    assertNotNull(dto);
    assertEquals(declaredActivity.getId(), dto.id());
    assertNotNull(dto.activity());
    assertEquals(activity.getId(), dto.activity().id());
    assertEquals(activity.getTitle(), dto.activity().title());
    assertNotNull(dto.status());
  }
}
