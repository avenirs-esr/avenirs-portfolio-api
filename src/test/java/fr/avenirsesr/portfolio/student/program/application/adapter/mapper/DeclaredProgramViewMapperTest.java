package fr.avenirsesr.portfolio.student.program.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.program.application.adapter.dto.DeclaredProgramViewDTO;
import fr.avenirsesr.portfolio.student.program.application.adapter.mapper.DeclaredProgramViewMapper;
import fr.avenirsesr.portfolio.student.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.program.domain.model.enums.EProgramStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class DeclaredProgramViewMapperTest {

  private final DeclaredProgramViewMapper mapper =
      Mappers.getMapper(DeclaredProgramViewMapper.class);

  @Test
  void shouldMapDeclaredProgramToViewDTO() {
    BddLogger.given("a declared program");
    Student student = StudentFixture.create().toModel();
    LocalDate startDate = LocalDate.now().minusYears(4);
    LocalDate endDate = LocalDate.now().minusYears(1);
    DeclaredProgram program =
        DeclaredProgram.create(
            student,
            EProgramStatus.COMPLETED,
            "Bachelor of Science",
            "Physics degree",
            "MIT",
            "Excellent",
            "Transcript",
            startDate,
            endDate);

    BddLogger.when("mapping to DeclaredProgramViewDTO");
    DeclaredProgramViewDTO dto = mapper.toDTO(program);

    BddLogger.then("it should return a correct DeclaredProgramViewDTO");
    assertNotNull(dto);
    assertEquals(program.getId(), dto.id());
    assertEquals(EProgramStatus.COMPLETED, dto.status());
    assertEquals("Bachelor of Science", dto.title());
    assertEquals("Physics degree", dto.description());
    assertEquals("MIT", dto.organization());
    assertEquals("Excellent", dto.result());
    assertEquals(startDate, dto.startDate());
    assertEquals(endDate, dto.endDate());
    assertEquals(program.isValorized(), dto.valorized());
  }
}
