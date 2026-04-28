package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.StudentProgressDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.fixture.StudentProgressFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class StudentProgressMapperTest {

  private final StudentProgressMapper mapper = Mappers.getMapper(StudentProgressMapper.class);

  @Test
  void shouldMapStudentProgressToDTO() {
    BddLogger.given("a student progress");
    StudentProgress studentProgress = StudentProgressFixture.create().toModel();

    BddLogger.when("mapping to StudentProgressDTO");
    StudentProgressDTO dto = mapper.toDto(studentProgress);

    BddLogger.then("it should map id, studentId, and training path");
    assertNotNull(dto);
    assertEquals(studentProgress.getId(), dto.id());
    assertEquals(studentProgress.getStudent().getId(), dto.studentId());
    assertNotNull(dto.trainingPath());
    assertEquals(studentProgress.getTrainingPath().getId(), dto.trainingPath().id());
    assertEquals(
        studentProgress.getTrainingPath().getProgram().getName(), dto.trainingPath().name());
  }
}
