package fr.avenirsesr.portfolio.activity.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ActivityServiceImplTest {

  @Mock private ActivityRepository activityRepository;

  @InjectMocks private ActivityServiceImpl activityService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void create_shouldReturnActivityAndSaveIt() {
    // Given
    UUID id = UUID.randomUUID();
    String title = "Test Activity";
    EActivityThematic thematic = EActivityThematic.EXPERIENCES;
    String summary = "This is a test activity";
    String executionPeriodInfo = "2026";

    // When
    Activity createdActivity =
        activityService.create(id, title, thematic, summary, executionPeriodInfo);

    // Then
    assertNotNull(createdActivity);
    assertEquals(id, createdActivity.getId());
    assertEquals(title, createdActivity.getTitle());
    assertEquals(thematic, createdActivity.getThematic());
    assertEquals(summary, createdActivity.getSummary());
    assertEquals(executionPeriodInfo, createdActivity.getExecutionPeriodInfo());

    // Verify repository was called
    ArgumentCaptor<Activity> captor = ArgumentCaptor.forClass(Activity.class);
    verify(activityRepository).save(captor.capture());
    Activity savedActivity = captor.getValue();
    assertEquals(createdActivity, savedActivity);
  }
}
