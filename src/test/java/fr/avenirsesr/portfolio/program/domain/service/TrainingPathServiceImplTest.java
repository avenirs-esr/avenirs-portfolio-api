package fr.avenirsesr.portfolio.program.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.program.domain.model.Program;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.port.output.TrainingPathRepository;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.ProgramFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.TrainingPathFixture;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TrainingPathServiceImplTest {
  private AutoCloseable closeable;
  private Student student;

  @Mock private TrainingPathRepository trainingPathRepository;

  @InjectMocks private TrainingPathServiceImpl trainingPathServiceImpl;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    student = UserFixture.createStudent().toModel().toStudent();
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void shouldReturnAllProgramProgressForStudent() {
    // Given
    Program program1 = ProgramFixture.create().withName("Beta").toModel();
    Program program2 = ProgramFixture.create().withName("Alpha").toModel();
    TrainingPath trainingPath1 = TrainingPathFixture.create().withProgram(program1).toModel();
    TrainingPath trainingPath2 = TrainingPathFixture.create().withProgram(program2).toModel();
    List<TrainingPath> allTrainingPaths = new ArrayList<>();
    allTrainingPaths.add(trainingPath1);
    allTrainingPaths.add(trainingPath2);

    when(trainingPathRepository.findAllTrainingPathsByStudents(any(Student.class)))
        .thenReturn(allTrainingPaths);

    // When
    List<TrainingPath> result = trainingPathServiceImpl.getTrainingPathsByStudent(student);

    // Then
    assertEquals(2, result.size());
    assertEquals("Alpha", result.get(0).getProgram().getName());
    assertEquals("Beta", result.get(1).getProgram().getName());
  }
}
