package fr.avenirsesr.portfolio.program.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.program.domain.model.Program;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.port.output.repository.TrainingPathRepository;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.ProgramFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.TrainingPathFixture;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class TrainingPathServiceImplTest {

  @Mock private StudentRepository studentRepository;
  @Mock private TrainingPathRepository trainingPathRepository;

  @InjectMocks private TrainingPathServiceImpl trainingPathServiceImpl;

  private Student student;
  private MockedStatic<RequestContext> mockedRequestContext;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
    mockedRequestContext = mockStatic(RequestContext.class);
    mockedRequestContext
        .when(RequestContext::get)
        .thenReturn(new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));

    when(studentRepository.findById(eq(student.getId()))).thenReturn(Optional.of(student));
  }

  @AfterEach
  void tearDown() throws Exception {
    mockedRequestContext.close();
  }

  @Test
  void shouldReturnAllProgramProgressForStudent() {
    BddLogger.given("a TrainingPathServiceImpl service");
    Program program1 = ProgramFixture.create().withName("Beta").toModel();
    Program program2 = ProgramFixture.create().withName("Alpha").toModel();
    TrainingPath trainingPath1 = TrainingPathFixture.create().withProgram(program1).toModel();
    TrainingPath trainingPath2 = TrainingPathFixture.create().withProgram(program2).toModel();
    List<TrainingPath> allTrainingPaths = new ArrayList<>();
    allTrainingPaths.add(trainingPath1);
    allTrainingPaths.add(trainingPath2);

    when(trainingPathRepository.findAllTrainingPathsByStudents(student))
        .thenReturn(allTrainingPaths);

    BddLogger.when("getting training paths for a student");
    List<TrainingPath> result = trainingPathServiceImpl.getTrainingPathsByStudent();

    BddLogger.then("it should return all program progresses for this student");
    assertEquals(2, result.size());
    assertEquals("Alpha", result.get(0).getProgram().getName());
    assertEquals("Beta", result.get(1).getProgram().getName());
  }
}
