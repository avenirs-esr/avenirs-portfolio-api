package fr.avenirsesr.portfolio.trace.application.adapter.controller;

import fr.avenirsesr.portfolio.shared.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.CreateTraceDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceOverviewDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.UnassociatedTracesSummaryDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceViewMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.UnassociatedTracesSummaryMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.response.TracesCreationResponse;
import fr.avenirsesr.portfolio.trace.application.adapter.response.TracesResponse;
import fr.avenirsesr.portfolio.trace.application.adapter.response.TracesViewResponse;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.TraceView;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceStatus;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.User;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/traces")
public class TraceController {
  private final TraceService traceService;
  private final UserUtil userUtil;

  @GetMapping("/overview")
  public ResponseEntity<List<TraceOverviewDTO>> getTraceOverview(Principal principal) {
    log.debug("Received request to trace overview of user [{}]", principal.getName());
    User user = userUtil.getUser(principal);

    List<Trace> traces = traceService.lastTracesOf(user);

    List<TraceOverviewDTO> response =
        traces.stream()
            .map(trace -> TraceOverviewMapper.toDTO(trace, traceService.programNameOfTrace(trace)))
            .toList();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/view")
  public ResponseEntity<TracesViewResponse> getTracesView(
      Principal principal,
      @RequestParam(required = false) ETraceStatus status,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to trace view of user [{}] (page= {}, size= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());
    User user = userUtil.getUser(principal);

    TracesViewResponse tracesViewResponse = null;

    if (status == ETraceStatus.UNASSOCIATED) {
      TraceView tracesView = traceService.getUnassociatedTraces(user, pageCriteria);

      tracesViewResponse =
          new TracesViewResponse(
              new TracesResponse(
                  tracesView.traces().stream()
                      .map(trace -> TraceViewMapper.toDTO(trace, ETraceStatus.UNASSOCIATED))
                      .toList(),
                  tracesView.criticalCount()),
              PageInfoDTO.fromDomain(tracesView.page()));
    }

    return ResponseEntity.ok(tracesViewResponse);
  }

  @DeleteMapping("/{traceId}")
  public ResponseEntity<String> deleteTrace(Principal principal, @PathVariable UUID traceId) {
    log.debug("Received request to delete trace [{}] of user [{}]", traceId, principal.getName());
    User user = userUtil.getUser(principal);

    traceService.deleteById(user, traceId);

    return ResponseEntity.ok("Resource successfully deleted.");
  }

  @GetMapping("/unassociated/summary")
  public ResponseEntity<UnassociatedTracesSummaryDTO> getTracesUnassociatedSummary(
      Principal principal) {
    log.debug(
        "Received request to get unassociated trace summary of user [{}]", principal.getName());
    User user = userUtil.getUser(principal);

    return ResponseEntity.ok(
        UnassociatedTracesSummaryMapper.toDTO(traceService.getUnassociatedTracesSummary(user)));
  }

  @PostMapping
  public ResponseEntity<TracesCreationResponse> createTrace(
      Principal principal, @Valid @RequestBody CreateTraceDTO createTraceDTO) {
    log.debug("Received request to create new trace for user [{}]", principal.getName());
    User user = userUtil.getUser(principal);

    var trace =
        traceService.createTrace(
            user,
            createTraceDTO.title(),
            createTraceDTO.language(),
            createTraceDTO.isGroup(),
            createTraceDTO.personalNote(),
            createTraceDTO.iaJustification());

    return ResponseEntity.status(201).body(new TracesCreationResponse(trace.getId()));
  }
}
