package fr.avenirsesr.portfolio.trace.application.adapter.controller;

import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.*;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.AssociationsTraceMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceAssociationSearchResultMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceDetailMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceViewMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TracesSummaryMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.response.TraceAssociationSearchResult;
import fr.avenirsesr.portfolio.trace.application.adapter.response.TracesCreationResponse;
import fr.avenirsesr.portfolio.trace.domain.model.AssociationsTrace;
import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.TraceDetail;
import fr.avenirsesr.portfolio.trace.domain.model.TracesSummary;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
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
  private final AMSService amsService;
  private final StudentProgressService studentProgressService;
  private final AdditionalSkillService additionalSkillService;
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
  public ResponseEntity<PagedResponse<TraceViewDTO>> getTracesView(
      Principal principal,
      @RequestParam(required = false) ETraceStatus status,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to trace view of user [{}] (page= {}, size= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());
    User user = userUtil.getUser(principal);

    PagedResult<Trace> tracesResult =
        traceService.getTracesView(user, pageCriteria, status, keyword);

    var tracesViewResponse =
        new PagedResponse<>(
            tracesResult.content().stream()
                .map(
                    trace ->
                        TraceViewMapper.toDTO(
                            trace, traceService.getWillBeDeletedAt(trace).orElse(null)))
                .toList(),
            PageInfoDTO.fromDomain(tracesResult.pageInfo()));

    return ResponseEntity.ok(tracesViewResponse);
  }

  @DeleteMapping("/{traceId}")
  public ResponseEntity<String> deleteTrace(Principal principal, @PathVariable UUID traceId) {
    log.debug("Received request to delete trace [{}] of user [{}]", traceId, principal.getName());
    User user = userUtil.getUser(principal);

    traceService.deleteById(user, traceId);

    return ResponseEntity.ok("Resource successfully deleted.");
  }

  @GetMapping("/summary")
  public ResponseEntity<TracesSummaryDTO> getTracesSummary(Principal principal) {
    log.debug("Received request to get trace summary of user [{}]", principal.getName());
    User user = userUtil.getUser(principal);

    TracesSummary summary = traceService.getTracesSummary(user);

    return ResponseEntity.ok(TracesSummaryMapper.toDTO(summary));
  }

  @GetMapping("/{traceId}/detail")
  public ResponseEntity<TraceDetailDTO> getTraceDetail(
      Principal principal, @PathVariable UUID traceId) {
    log.debug("Received request to get trace detail of user [{}]", principal.getName());
    User user = userUtil.getUser(principal);

    TraceDetail traceDetail = traceService.getTraceDetail(user, traceId);

    return ResponseEntity.ok(TraceDetailMapper.toDTO(traceDetail));
  }

  @GetMapping("/{traceId}/associations")
  public ResponseEntity<AssociationsTraceDTO> getTraceAssociations(
      Principal principal, @PathVariable UUID traceId) {
    log.debug("Received request to get trace associations of user [{}]", principal.getName());
    User user = userUtil.getUser(principal);

    AssociationsTrace associationsTrace = traceService.getAssociationsTrace(user, traceId);

    return ResponseEntity.ok(AssociationsTraceMapper.toDTO(associationsTrace));
  }

  @PostMapping("/associate/{traceId}")
  public ResponseEntity<String> associate(
      Principal principal,
      @PathVariable UUID traceId,
      @RequestBody AssociateTraceDTO associateTraceDTO) {
    log.info("User [{}] request to associate trace [{}]", principal.getName(), traceId);

    User user = userUtil.getUser(principal);

    traceService.associateTrace(
        user,
        traceId,
        associateTraceDTO.amsIds(),
        associateTraceDTO.skillLevelIds(),
        associateTraceDTO.additionalSkillProgressIds());

    return ResponseEntity.ok("Trace successfully associated.");
  }

  @GetMapping("/search-association/{associationType}")
  public ResponseEntity<PagedResponse<TraceAssociationSearchResult>> getAssociatedTraces(
      Principal principal,
      @PathVariable ETraceAssociationType associationType,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    Student student = userUtil.getStudent(principal);

    PagedResult<TraceAssociationSearchResult> results =
        new PagedResult<>(List.of(), new PageInfo(page, pageSize, 0));

    switch (associationType) {
      case AMS -> {
        var amses = amsService.search(student, keyword, pageCriteria);
        results =
            new PagedResult<>(
                amses.content().stream().map(TraceAssociationSearchResultMapper::toDTO).toList(),
                amses.pageInfo());
      }
      case SKILL_LEVEL -> {
        var skillLevelProgresses = studentProgressService.search(student, keyword, pageCriteria);
        results =
            new PagedResult<>(
                skillLevelProgresses.content().stream()
                    .map(TraceAssociationSearchResultMapper::toDTO)
                    .toList(),
                skillLevelProgresses.pageInfo());
      }
      case ADDITIONAL_SKILL -> {
        var additionalSkills = additionalSkillService.search(student, keyword, pageCriteria);
        results =
            new PagedResult<>(
                additionalSkills.content().stream()
                    .map(TraceAssociationSearchResultMapper::toDTO)
                    .toList(),
                additionalSkills.pageInfo());
      }
    }

    return ResponseEntity.ok(
        new PagedResponse<>(results.content(), PageInfoDTO.fromDomain(results.pageInfo())));
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
