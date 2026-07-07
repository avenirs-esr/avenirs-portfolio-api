package fr.avenirsesr.portfolio.trace.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.CreateTraceDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceDetailDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceOverviewDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.UpdateTraceDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceDetailMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.response.TracesCreationResponse;
import fr.avenirsesr.portfolio.trace.domain.data.TraceDetailData;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.infrastructure.fixture.TraceFixture;
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
  @Mock private TraceOverviewMapper traceOverviewMapper;
  @Mock private TraceDetailMapper traceDetailMapper;

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
    when(traceService.lastTracesOf()).thenReturn(List.of(trace));
    when(traceService.programNameOfTrace(trace)).thenReturn("Program Name");
    when(traceOverviewMapper.toDTO(trace, "Program Name"))
        .thenReturn(
            new TraceOverviewDTO(
                trace.getId(),
                trace.getTitle(),
                "Program Name",
                ETraceAuthorType.PERSONAL,
                null,
                null));

    BddLogger.when("getting the trace overview");
    ResponseEntity<List<TraceOverviewDTO>> response = controller.getTraceOverview(principal);

    BddLogger.then("it should return the trace overview");
    assertEquals(200, response.getStatusCode().value());

    List<TraceOverviewDTO> body = response.getBody();
    assertNotNull(body);
    assertEquals(1, body.size());

    TraceOverviewDTO dto = body.getFirst();
    assertEquals(trace.getId(), dto.id());
    assertEquals(trace.getTitle(), dto.title());
    assertEquals("Program Name", dto.programName());

    verify(traceService).lastTracesOf();
    verify(traceService).programNameOfTrace(trace);
  }

  @Test
  void shouldCreateTraceSuccessfully() {
    BddLogger.given("a TraceController");
    when(traceService.createTrace(
            anyString(),
            any(ELanguage.class),
            any(ETraceAuthorType.class),
            anyString(),
            anyString(),
            anyString()))
        .thenReturn(trace);

    CreateTraceDTO dto =
        new CreateTraceDTO(
            "My Trace",
            ELanguage.FRENCH,
            ETraceAuthorType.COLLECTIVE,
            "Personal note",
            "Justification IA",
            "https://example.com");

    BddLogger.when("creating a trace");
    ResponseEntity<TracesCreationResponse> response = controller.createTrace(principal, dto);

    BddLogger.then("it should create the trace successfully");
    assertEquals(201, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(trace.getId(), response.getBody().traceId());

    verify(traceService)
        .createTrace(
            eq("My Trace"),
            eq(ELanguage.FRENCH),
            eq(ETraceAuthorType.COLLECTIVE),
            eq("Personal note"),
            eq("Justification IA"),
            eq("https://example.com"));
  }

  @Test
  void shouldCreateTraceWithNullFields() {
    BddLogger.given("a TraceController");
    when(traceService.createTrace(
            "Trace sans IA", ELanguage.FRENCH, ETraceAuthorType.PERSONAL, null, null, null))
        .thenReturn(trace);

    CreateTraceDTO dto =
        new CreateTraceDTO(
            "Trace sans IA", ELanguage.FRENCH, ETraceAuthorType.PERSONAL, null, null, null);

    BddLogger.when("creating a trace with null fields");
    ResponseEntity<TracesCreationResponse> response = controller.createTrace(principal, dto);

    BddLogger.then("it should create the trace with null fields successfully");
    assertEquals(201, response.getStatusCode().value());

    verify(traceService)
        .createTrace(
            "Trace sans IA", ELanguage.FRENCH, ETraceAuthorType.PERSONAL, null, null, null);
  }

  @Test
  void shouldUpdateTraceWithValorisedTrue() {
    BddLogger.given("a TraceController");
    UUID traceId = UUID.randomUUID();
    TraceDetailData traceDetailData = mock(TraceDetailData.class);
    TraceDetailDTO traceDetailDTO = mock(TraceDetailDTO.class);

    when(traceService.updateTrace(
            eq(traceId),
            eq("Updated title"),
            eq(ELanguage.FRENCH),
            eq(ETraceAuthorType.PERSONAL),
            eq("Updated note"),
            eq("Updated justification"),
            isNull(),
            eq(true)))
        .thenReturn(traceDetailData);
    when(traceDetailMapper.toDTO(traceDetailData)).thenReturn(traceDetailDTO);

    UpdateTraceDTO dto =
        new UpdateTraceDTO(
            "Updated title",
            ELanguage.FRENCH,
            ETraceAuthorType.PERSONAL,
            "Updated note",
            "Updated justification",
            null,
            true);

    BddLogger.when("updating a trace with valorized to true");
    ResponseEntity<TraceDetailDTO> response = controller.updateTrace(principal, traceId, dto);

    BddLogger.then("it should update the trace with valorized to true");
    assertEquals(200, response.getStatusCode().value());
    assertEquals(traceDetailDTO, response.getBody());

    verify(traceService)
        .updateTrace(
            eq(traceId),
            eq("Updated title"),
            eq(ELanguage.FRENCH),
            eq(ETraceAuthorType.PERSONAL),
            eq("Updated note"),
            eq("Updated justification"),
            isNull(),
            eq(true));
    verify(traceDetailMapper).toDTO(traceDetailData);
  }

  @Test
  void shouldUpdateTraceWithValorisedFalse() {
    BddLogger.given("a TraceController");
    UUID traceId = UUID.randomUUID();
    TraceDetailData traceDetailData = mock(TraceDetailData.class);
    TraceDetailDTO traceDetailDTO = mock(TraceDetailDTO.class);

    when(traceService.updateTrace(
            eq(traceId),
            eq("Updated title"),
            eq(ELanguage.FRENCH),
            eq(ETraceAuthorType.PERSONAL),
            eq("Updated note"),
            eq("Updated justification"),
            isNull(),
            eq(false)))
        .thenReturn(traceDetailData);
    when(traceDetailMapper.toDTO(traceDetailData)).thenReturn(traceDetailDTO);

    UpdateTraceDTO dto =
        new UpdateTraceDTO(
            "Updated title",
            ELanguage.FRENCH,
            ETraceAuthorType.PERSONAL,
            "Updated note",
            "Updated justification",
            null,
            false);

    BddLogger.when("updating a trace with valorized to false");
    ResponseEntity<TraceDetailDTO> response = controller.updateTrace(principal, traceId, dto);

    BddLogger.then("it should update the trace with valorized to false");
    assertEquals(200, response.getStatusCode().value());
    assertEquals(traceDetailDTO, response.getBody());

    verify(traceService)
        .updateTrace(
            eq(traceId),
            eq("Updated title"),
            eq(ELanguage.FRENCH),
            eq(ETraceAuthorType.PERSONAL),
            eq("Updated note"),
            eq("Updated justification"),
            isNull(),
            eq(false));
  }
}
