package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.DeclaredProgramViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
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
    DeclaredProgram program =
        DeclaredProgram.create(
            student,
            EProgramStatus.COMPLETED,
            "Bachelor of Science",
            "Physics degree",
            "MIT",
            "Excellent",
            "Transcript",
            LocalDate.now().minusYears(4),
            LocalDate.now().minusYears(1));

    BddLogger.when("mapping to DeclaredProgramViewDTO");
    DeclaredProgramViewDTO dto = mapper.toDTO(program);

    BddLogger.then("it should return a correct DeclaredProgramViewDTO");
    assertNotNull(dto);
    assertEquals(program.getId(), dto.id());
    assertEquals(EProgramStatus.COMPLETED, dto.status());
    assertEquals("Bachelor of Science", dto.title());
    assertEquals("MIT", dto.organization());
  }
}
