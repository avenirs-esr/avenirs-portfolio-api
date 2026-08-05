package fr.avenirsesr.portfolio.student.program.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.program.application.adapter.dto.DeclaredProgramDetailedDTO;
import fr.avenirsesr.portfolio.student.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.program.domain.model.enums.EProgramStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class DeclaredProgramDetailedMapperTest {

  private final DeclaredProgramDetailedMapper mapper =
      Mappers.getMapper(DeclaredProgramDetailedMapper.class);

  @Test
  void shouldMapDeclaredProgramToDetailedDTO() {
    BddLogger.given("a declared program");
    Student student = StudentFixture.create().toModel();
    DeclaredProgram program =
        DeclaredProgram.create(
            student,
            EProgramStatus.IN_PROGRESS,
            "Master Computer Science",
            "A great program",
            "University of Paris",
            "Graduated with honors",
            "Self",
            LocalDate.now().minusYears(2),
            LocalDate.now());

    BddLogger.when("mapping to DeclaredProgramDetailedDTO");
    DeclaredProgramDetailedDTO dto = mapper.toDTO(program);

    BddLogger.then("it should return a correct DeclaredProgramDetailedDTO");
    assertNotNull(dto);
    assertEquals(program.getId(), dto.id());
    assertEquals(EProgramStatus.IN_PROGRESS, dto.status());
    assertEquals("Master Computer Science", dto.title());
    assertEquals("University of Paris", dto.organization());
    assertFalse(dto.valorized());
  }

  @Test
  void shouldMapValorizedDeclaredProgramToDetailedDTO() {
    BddLogger.given("a valorized declared program");
    Student student = StudentFixture.create().toModel();
    DeclaredProgram program =
        DeclaredProgram.of(
            UUID.randomUUID(),
            student,
            EProgramStatus.IN_PROGRESS,
            "Master Computer Science",
            "A great program",
            "University of Paris",
            "Graduated with honors",
            "Self",
            LocalDate.now().minusYears(2),
            LocalDate.now(),
            true,
            Instant.now(),
            Instant.now());

    BddLogger.when("mapping to DeclaredProgramDetailedDTO");
    DeclaredProgramDetailedDTO dto = mapper.toDTO(program);

    BddLogger.then("it should reflect the valorized flag");
    assertTrue(dto.valorized());
  }
}
