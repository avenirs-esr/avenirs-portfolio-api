package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.AdditionalSkillSeeder;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.AdditionalSkillProgressSeeder;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.TraceSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.StudentSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TraceAttachmentSeederTest {

  @Autowired private TraceAttachmentSeeder traceAttachmentSeeder;

  @Autowired private TraceSeeder traceSeeder;

  @Autowired private UserSeeder userSeeder;
  @Autowired StudentSeeder studentSeeder;
  @Autowired private AdditionalSkillProgressSeeder additionalSkillProgressSeeder;
  @Autowired private AdditionalSkillSeeder additionalSkillSeeder;

  private List<TraceEntity> traces;

  @BeforeAll
  void setUp() {
    // Seed des utilisateurs
    var users = userSeeder.seed();
    var students = studentSeeder.seed(users);
    var additionalSkills = additionalSkillSeeder.seed();
    List<AdditionalSkillProgressEntity> additionalSkillProgresses =
        additionalSkillProgressSeeder.seed(students, additionalSkills);
    // Seed des traces pour les utilisateurs
    this.traces = traceSeeder.seed(users, additionalSkillProgresses);
  }

  @Test
  void seed_shouldThrowException_whenTracesEmpty() {
    BddLogger.given("a trace attachment seeder");
    BddLogger.when("there is no traces");
    BddLogger.then("it should throw IllegalArgumentException");
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> traceAttachmentSeeder.seed(List.of()));
    assertTrue(exception.getMessage().contains("traces cannot be empty"));
  }

  @Test
  void seed_shouldReturnAttachments_forAllTraces() {
    BddLogger.given("a trace attachment seeder");
    BddLogger.when("there is a list of traces");
    List<TraceAttachmentEntity> attachments = traceAttachmentSeeder.seed(traces);

    BddLogger.then("it should return attachments for all traces");
    assertNotNull(attachments);
    assertFalse(attachments.isEmpty());

    // Vérifie qu'il y a au moins un attachment par trace
    for (TraceEntity trace : traces) {
      boolean hasAttachment =
          attachments.stream().anyMatch(att -> att.getTrace().getId().equals(trace.getId()));
      assertTrue(hasAttachment, "Trace " + trace.getId() + " doit avoir au moins un attachment");
    }

    // Vérifie que les versions sont correctes
    attachments.forEach(
        att -> {
          assertTrue(att.getVersion() > 0);
          assertNotNull(att.getTrace());
        });
  }
}
