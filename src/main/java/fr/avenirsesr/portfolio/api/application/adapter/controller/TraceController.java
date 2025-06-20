package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.TraceOverviewDTO;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.TraceOverviewMapper;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.TraceViewMapper;
import fr.avenirsesr.portfolio.api.application.adapter.response.TracesResponse;
import fr.avenirsesr.portfolio.api.application.adapter.response.TracesViewResponse;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.TraceView;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.ETraceStatus;
import fr.avenirsesr.portfolio.api.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/traces")
public class TraceController {
  private final UserRepository userRepository;
  private final TraceService traceService;

  @GetMapping("/overview")
  public ResponseEntity<List<TraceOverviewDTO>> getTraceOverview(Principal principal) {
    log.debug("Received request to trace overview of user [{}]", principal.getName());
    User user = getUser(principal.getName());

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
    log.debug("Received request to trace view of user [{}]", principal.getName());
    User user = getUser(principal.getName());

    TracesViewResponse tracesViewResponse = null;

    if (status == ETraceStatus.UNASSOCIATED) {
      TraceView tracesView = traceService.getUnassociatedTraces(user, page, pageSize);

      tracesViewResponse =
          new TracesViewResponse(
              new TracesResponse(
                  tracesView.traces().stream()
                      .map(trace -> TraceViewMapper.toDTO(trace, ETraceStatus.UNASSOCIATED))
                      .toList(),
                  tracesView.criticalCount()),
              tracesView.page());
    }

    return ResponseEntity.ok(tracesViewResponse);
  }

  @DeleteMapping("/{traceId}")
  public ResponseEntity<String> deleteTrace(Principal principal, @PathVariable UUID traceId) {
    log.debug("Received request to trace overview of user [{}]", principal.getName());
    User user = getUser(principal.getName());

    traceService.deleteById(user, traceId);

    return ResponseEntity.ok("Resource successfully deleted.");
  }

  private User getUser(String id) {
    UUID userId = UUID.fromString(id);
    return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
  }
}
