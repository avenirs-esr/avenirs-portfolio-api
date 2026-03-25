package fr.avenirsesr.portfolio.trace.application.adapter.controller;

import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.AssociationsCreationRequest;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.*;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.*;
import fr.avenirsesr.portfolio.trace.application.adapter.response.TraceAssociationSearchResult;
import fr.avenirsesr.portfolio.trace.application.adapter.response.TracesCreationResponse;
import fr.avenirsesr.portfolio.trace.domain.data.TraceDetailData;
import fr.avenirsesr.portfolio.trace.domain.data.TracesSummaryData;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
  private final DeclaredSkillProgressService declaredSkillProgressService;

  @GetMapping("/overview")
  public ResponseEntity<List<TraceOverviewDTO>> getTraceOverview(Principal principal) {
    log.debug("Received request to trace overview of user [{}]", principal.getName());
    List<Trace> traces = traceService.lastTracesOf();

    List<TraceOverviewDTO> response =
        traces.stream()
            .map(trace -> TraceOverviewMapper.toDTO(trace, traceService.programNameOfTrace(trace)))
            .toList();

    return ResponseEntity.ok(response);
  }

  @PostMapping("/view")
  public ResponseEntity<PagedResponse<TraceViewDTO>> tracesView(
      Principal principal,
      @RequestBody TraceFilter traceFilter,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) LocalDate fromDate,
      @RequestParam(required = false) LocalDate toDate) {
    var dateFilter = new DateFilter(fromDate, toDate);
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to trace view of user [{}] (page= {}, size= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());
    PagedResult<Trace> tracesResult =
        traceService.getTracesView(keyword, traceFilter, dateFilter, pageCriteria);

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

    traceService.deleteById(traceId);

    return ResponseEntity.ok("Resource successfully deleted.");
  }

  @GetMapping("/summary")
  public ResponseEntity<TracesSummaryDTO> getTracesSummary(Principal principal) {
    log.debug("Received request to get trace summary of user [{}]", principal.getName());

    TracesSummaryData summary = traceService.getTracesSummary();

    return ResponseEntity.ok(TracesSummaryMapper.toDTO(summary));
  }

  @GetMapping("/{traceId}/detail")
  public ResponseEntity<TraceDetailDTO> getTraceDetail(
      Principal principal, @PathVariable UUID traceId) {
    log.debug(
        "Received request to get trace [{}] detail of user [{}]", traceId, principal.getName());

    TraceDetailData traceDetail = traceService.getTraceDetail(traceId);

    return ResponseEntity.ok(TraceDetailMapper.toDTO(traceDetail));
  }

  @GetMapping("/search-association/{associationType}")
  public ResponseEntity<PagedResponse<TraceAssociationSearchResult>> getAssociatedTraces(
      @PathVariable ETraceAssociationType associationType,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    PagedResult<TraceAssociationSearchResult> results =
        new PagedResult<>(List.of(), new PageInfo(page, pageSize, 0));

    switch (associationType) {
      case AMS -> {
        var amses = amsService.search(keyword, pageCriteria);
        results =
            new PagedResult<>(
                amses.content().stream().map(TraceAssociationSearchResultMapper::toDTO).toList(),
                amses.pageInfo());
      }
      case SKILL_LEVEL -> {
        var skillLevelProgresses = studentProgressService.searchSkillLevel(keyword, pageCriteria);
        results =
            new PagedResult<>(
                skillLevelProgresses.content().stream()
                    .map(TraceAssociationSearchResultMapper::toDTO)
                    .toList(),
                skillLevelProgresses.pageInfo());
      }
      case DECLARED_SKILL -> {
        var declaredSkills =
            declaredSkillProgressService.searchDeclaredSkill(keyword, pageCriteria);
        results =
            new PagedResult<>(
                declaredSkills.content().stream()
                    .map(TraceAssociationSearchResultMapper::toDTO)
                    .toList(),
                declaredSkills.pageInfo());
      }
    }

    return ResponseEntity.ok(
        new PagedResponse<>(results.content(), PageInfoDTO.fromDomain(results.pageInfo())));
  }

  @PostMapping
  public ResponseEntity<TracesCreationResponse> createTrace(
      Principal principal, @Valid @RequestBody CreateTraceDTO createTraceDTO) {
    log.debug("Received request to create new trace for user [{}]", principal.getName());
    var trace =
        traceService.createTrace(
            createTraceDTO.title(),
            createTraceDTO.language(),
            createTraceDTO.isGroup(),
            createTraceDTO.personalNote(),
            createTraceDTO.iaJustification());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new TracesCreationResponse(trace.getId()));
  }

  @PutMapping("/{traceId}")
  public ResponseEntity<TraceDetailDTO> updateTrace(
      Principal principal,
      @PathVariable UUID traceId,
      @Valid @RequestBody UpdateTraceDTO updateTraceDTO) {
    log.debug("Received request to update trace [{}] for user [{}]", traceId, principal.getName());
    var trace =
        traceService.updateTrace(
            traceId,
            updateTraceDTO.title(),
            updateTraceDTO.language(),
            updateTraceDTO.isGroup(),
            updateTraceDTO.personalNote(),
            updateTraceDTO.iaJustification());

    return ResponseEntity.ok(TraceDetailMapper.toDTO(trace));
  }

  @PostMapping("/{traceId}/associate/activities")
  public ResponseEntity<TraceAssociationsDTO> associateTraceWithActivities(
      Principal principal,
      @Valid @PathVariable UUID traceId,
      @Valid @RequestBody AssociationsCreationRequest body) {
    log.debug(
        "Received request to associate Trace[{}] with activities [{}] by student [{}]",
        traceId,
        body.idsToAssociate(),
        principal.getName());
    var traceAssociations =
        traceService.associateTraceWithActivities(traceId, body.idsToAssociate());
    return ResponseEntity.ok(TraceAssociationsMapper.toDTO(traceAssociations));
  }

  @PostMapping("/{traceId}/associate/declared-skill")
  public ResponseEntity<TraceAssociationsDTO> associateTraceWithDeclaredSkill(
      Principal principal,
      @Valid @PathVariable UUID traceId,
      @Valid @RequestBody AssociationsCreationRequest body) {
    log.debug(
        "Received request to associate Trace[{}] with declared skill [{}] by student [{}]",
        traceId,
        body.idsToAssociate(),
        principal.getName());
    var traceAssociations =
        traceService.associateTraceWithDeclaredSkill(traceId, body.idsToAssociate());
    return ResponseEntity.ok(TraceAssociationsMapper.toDTO(traceAssociations));
  }

  @GetMapping("/{traceId}/associations")
  public ResponseEntity<TraceAssociationsDTO> getTraceAssociations(
      Principal principal, @Valid @PathVariable UUID traceId) {
    log.debug(
        "Received request to get associate Trace[{}] associations by student [{}]",
        traceId,
        principal.getName());

    var traceAssociations = traceService.getTraceAssociations(traceId);
    return ResponseEntity.ok(TraceAssociationsMapper.toDTO(traceAssociations));
  }

  @DeleteMapping("/{traceId}/associations")
  public ResponseEntity<String> deleteTraceAssociations(
      Principal principal,
      @Valid @PathVariable UUID traceId,
      @RequestBody List<UUID> associationIds) {
    log.debug(
        "Received request to unassociate associations [{}] to trace [{}] for student [{}]",
        associationIds,
        traceId,
        principal.getName());

    traceService.unassociate(traceId, associationIds);

    return ResponseEntity.ok("Associations successfully deleted.");
  }
}
