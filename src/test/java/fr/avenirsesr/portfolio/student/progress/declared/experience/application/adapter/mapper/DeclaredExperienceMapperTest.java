package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredExperienceMapperTest {

  @Spy
  private fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper
      traceOverviewMapper =
          Mappers.getMapper(
              fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper.class);

  @InjectMocks private DeclaredExperienceMapperImpl mapper;

  @Test
  void shouldMapDeclaredExperienceToViewDTO() {
    BddLogger.given("a declared experience");
    Student student = StudentFixture.create().toModel();
    DeclaredExperience experience =
        DeclaredExperience.create(
            student,
            "Software Experience",
            EExperienceType.PROFESSIONAL,
            "Tech Corp",
            "Software",
            "Paris",
            "Backend development",
            "LinkedIn",
            "Built REST APIs",
            "https://techcorp.com",
            LocalDate.now().minusMonths(6),
            LocalDate.now());

    BddLogger.when("mapping to DeclaredExperienceViewDTO");
    DeclaredExperienceViewDTO dto = mapper.toDTO(experience);

    BddLogger.then("it should return a correct DeclaredExperienceViewDTO");
    assertNotNull(dto);
    assertEquals(experience.getId(), dto.id());
    assertEquals(experience.getTitle(), dto.title());
    assertEquals(EExperienceType.PROFESSIONAL, dto.experienceType());
    assertEquals("Tech Corp", dto.organization());
  }
}
