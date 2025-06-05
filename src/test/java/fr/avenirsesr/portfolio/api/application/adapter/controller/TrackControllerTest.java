package fr.avenirsesr.portfolio.api.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fixtures.TrackFixture;
import fixtures.UserFixture;
import fr.avenirsesr.portfolio.api.application.adapter.dto.TrackOverviewDTO;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.api.domain.model.*;
import fr.avenirsesr.portfolio.api.domain.port.input.TrackService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TrackControllerTest {

  @Mock private UserRepository userRepository;
  @Mock private TrackService trackService;

  @InjectMocks private TrackController controller;

  private UUID userId;
  private User user;
  private Track track;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = UserFixture.create().withId(userId).toModel();
    track = TrackFixture.create().withUser(user).toModel();
  }

  @Test
  void shouldReturnTrackOverviewForUser() {
    // Given
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(trackService.lastTracksOf(user)).thenReturn(List.of(track));
    when(trackService.programNameOfTrack(track)).thenReturn("Program Name");

    // When
    ResponseEntity<List<TrackOverviewDTO>> response =
        controller.getTrackOverview(userId.toString());

    // Then
    assertEquals(200, response.getStatusCode().value());

    List<TrackOverviewDTO> body = response.getBody();
    assertNotNull(body);
    assertEquals(1, body.size());

    TrackOverviewDTO dto = body.getFirst();
    assertEquals(track.getId(), dto.trackId());
    assertEquals(track.getTitle(), dto.title());
    assertEquals("Program Name", dto.programName());

    verify(userRepository).findById(userId);
    verify(trackService).lastTracksOf(user);
    verify(trackService).programNameOfTrack(track);
  }

  @Test
  void shouldThrowUserNotFoundExceptionIfUserDoesNotExist() {
    // Given
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Then
    assertThrows(UserNotFoundException.class, () -> controller.getTrackOverview(userId.toString()));

    verify(userRepository).findById(userId);
    verifyNoMoreInteractions(trackService);
  }
}
