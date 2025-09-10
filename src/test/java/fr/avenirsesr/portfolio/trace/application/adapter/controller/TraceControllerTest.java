package fr.avenirsesr.portfolio.trace.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.testutils.BddLogger;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.CreateTraceDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceOverviewDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.response.TracesCreationResponse;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.infrastructure.fixture.TraceFixture;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
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

  @Mock private TraceService traceService;
  @Mock private UserUtil userUtil;

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
  void shouldReturnTraceOverviewForUser() {
    BddLogger.given("a TraceController");
    when(traceService.lastTracesOf(user)).thenReturn(List.of(trace));
    when(traceService.programNameOfTrace(trace)).thenReturn("Program Name");
    when(userUtil.getUser(any())).thenReturn(user);

    BddLogger.when("getting the trace overview");
    ResponseEntity<List<TraceOverviewDTO>> response = controller.getTraceOverview(principal);

    BddLogger.then("it should return the trace overview");
    assertEquals(200, response.getStatusCode().value());

    List<TraceOverviewDTO> body = response.getBody();
    assertNotNull(body);
    assertEquals(1, body.size());

    TraceOverviewDTO dto = body.getFirst();
    assertEquals(trace.getId(), dto.traceId());
    assertEquals(trace.getTitle(), dto.title());
    assertEquals("Program Name", dto.programName());

    verify(userUtil).getUser(principal);
    verify(traceService).lastTracesOf(user);
    verify(traceService).programNameOfTrace(trace);
  }

  @Test
  void shouldCreateTraceSuccessfully() {
    BddLogger.given("a TraceController");
    when(userUtil.getUser(any())).thenReturn(user);
    when(traceService.createTrace(
            any(User.class),
            anyString(),
            any(ELanguage.class),
            anyBoolean(),
            anyString(),
            anyString()))
        .thenReturn(trace);

    CreateTraceDTO dto =
        new CreateTraceDTO("My Trace", ELanguage.FRENCH, true, "Personal note", "Justification IA");

    BddLogger.when("creating a trace");
    ResponseEntity<TracesCreationResponse> response = controller.createTrace(principal, dto);

    BddLogger.then("it should create the trace successfully");
    assertEquals(201, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(trace.getId(), response.getBody().traceId());

    verify(userUtil).getUser(principal);
    verify(traceService)
        .createTrace(
            eq(user),
            eq("My Trace"),
            eq(ELanguage.FRENCH),
            eq(true),
            eq("Personal note"),
            eq("Justification IA"));
  }

  @Test
  void shouldCreateTraceWithNullFields() {
    BddLogger.given("a TraceController");

    when(userUtil.getUser(any())).thenReturn(user);
    when(traceService.createTrace(user, "Trace sans IA", ELanguage.FRENCH, false, null, null))
        .thenReturn(trace);

    CreateTraceDTO dto = new CreateTraceDTO("Trace sans IA", ELanguage.FRENCH, false, null, null);

    BddLogger.when("creating a trace with null fields");
    ResponseEntity<TracesCreationResponse> response = controller.createTrace(principal, dto);

    BddLogger.then("it should create the trace with null fields successfully");
    assertEquals(201, response.getStatusCode().value());

    verify(traceService).createTrace(user, "Trace sans IA", ELanguage.FRENCH, false, null, null);
  }
}
