package fr.avenirsesr.portfolio.api.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fixtures.TraceFixture;
import fixtures.UserFixture;
import fr.avenirsesr.portfolio.api.application.adapter.dto.TraceOverviewDTO;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.api.domain.model.*;
import fr.avenirsesr.portfolio.api.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import java.security.Principal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TraceControllerTest {

  @Mock private UserRepository userRepository;
  @Mock private TraceService traceService;

  @InjectMocks private TraceController controller;

  private UUID userId;
  private User user;
  private Trace trace;
  private Principal principal;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = UserFixture.create().withId(userId).toModel();
    trace = TraceFixture.create().withUser(user).toModel();
    principal = () -> userId.toString();
  }

  @Test
  void shouldReturnTrackOverviewForUser() {
    // Given
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(traceService.lastTracesOf(user)).thenReturn(List.of(trace));
    when(traceService.programNameOfTrace(trace)).thenReturn("Program Name");

    // When
    ResponseEntity<List<TraceOverviewDTO>> response = controller.getTraceOverview(principal);

    // Then
    assertEquals(200, response.getStatusCode().value());

    List<TraceOverviewDTO> body = response.getBody();
    assertNotNull(body);
    assertEquals(1, body.size());

    TraceOverviewDTO dto = body.getFirst();
    assertEquals(trace.getId(), dto.traceId());
    assertEquals(trace.getTitle(), dto.title());
    assertEquals("Program Name", dto.programName());

    verify(userRepository).findById(userId);
    verify(traceService).lastTracesOf(user);
    verify(traceService).programNameOfTrace(trace);
  }

  @Test
  void shouldThrowUserNotFoundExceptionIfUserDoesNotExist() {
    // Given
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Then
    assertThrows(UserNotFoundException.class, () -> controller.getTraceOverview(principal));

    verify(userRepository).findById(userId);
    verifyNoMoreInteractions(traceService);
  }
}
