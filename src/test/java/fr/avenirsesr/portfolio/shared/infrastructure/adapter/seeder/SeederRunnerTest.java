package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.CohortRepository;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.UserPhotoRepository;
import fr.avenirsesr.portfolio.program.domain.port.output.InstitutionRepository;
import fr.avenirsesr.portfolio.program.domain.port.output.ProgramRepository;
import fr.avenirsesr.portfolio.program.domain.port.output.SkillRepository;
import fr.avenirsesr.portfolio.program.domain.port.output.TrainingPathRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class SeederRunnerTest {
  @MockBean private UserRepository userRepository; // mock DB
  @MockBean private UserPhotoRepository userPhotoRepository; // mock DB

  // ⚡ tous les autres repositories que les seeders utilisent doivent aussi être mockés
  @MockBean private CohortRepository cohortRepository;
  @MockBean private AMSRepository amsRepository;
  @MockBean private TraceRepository traceRepository;
  @MockBean private TraceAttachmentRepository traceAttachmentRepository;
  @MockBean private InstitutionRepository institutionRepository;
  @MockBean private ProgramRepository programRepository;
  @MockBean private TrainingPathRepository trainingPathRepository;
  @MockBean private StudentProgressRepository studentProgressRepository;
  @MockBean private SkillRepository skillRepository;

  @Autowired private SeederRunner seederRunner;

  @BeforeEach
  void setUp() {
    when(userRepository.countAll()).thenReturn(0L); // force le seed
  }

  @Test
  void shouldGenerateSameUserPhotosAcrossRuns() {
    // Run 1
    seederRunner.run();
    ArgumentCaptor<List<UserPhoto>> captor1 = ArgumentCaptor.forClass(List.class);
    verify(userPhotoRepository, atLeastOnce()).saveAll(captor1.capture());
    List<UserPhoto> firstRunPhotos = captor1.getAllValues().stream().flatMap(List::stream).toList();

    // Reset interactions
    clearInvocations(userPhotoRepository);

    // Run 2
    seederRunner.run();
    ArgumentCaptor<List<UserPhoto>> captor2 = ArgumentCaptor.forClass(List.class);
    verify(userPhotoRepository, atLeastOnce()).saveAll(captor2.capture());
    List<UserPhoto> secondRunPhotos =
        captor2.getAllValues().stream().flatMap(List::stream).toList();

    // Assertions
    assertEquals(firstRunPhotos.size(), secondRunPhotos.size(), "Same number of photos");
    for (int i = 0; i < firstRunPhotos.size(); i++) {
      assertEquals(
          firstRunPhotos.get(i).getId(),
          secondRunPhotos.get(i).getId(),
          "IDs should be deterministic across runs");
    }
  }
}
