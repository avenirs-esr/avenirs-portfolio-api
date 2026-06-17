package fr.avenirsesr.portfolio.trace.application.adapter.controller;

import fr.avenirsesr.portfolio.association.application.adapter.dto.AssociationSearchResultDeclaredActivityDTO;
import fr.avenirsesr.portfolio.association.application.adapter.dto.AssociationSearchResultDeclaredExperienceDTO;
import fr.avenirsesr.portfolio.association.application.adapter.dto.AssociationSearchResultDeclaredSkillIDTO;
import fr.avenirsesr.portfolio.association.application.adapter.mapper.AssociationSearchResultDTOMapper;
import fr.avenirsesr.portfolio.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.AssociationsCreationRequest;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.*;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.*;
import fr.avenirsesr.portfolio.trace.application.adapter.response.TracesCreationResponse;
import fr.avenirsesr.portfolio.trace.domain.data.*;
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
  private final TraceViewMapper traceViewMapper;
  private final TraceOverviewMapper traceOverviewMapper;
  private final TraceDetailMapper traceDetailMapper;
  private final TraceDeclaredActivityMapper traceDeclaredActivityMapper;
  private final TraceLockedDeclaredActivitiesMapper traceLockedDeclaredActivitiesMapper;
  private final TracesSummaryMapper tracesSummaryMapper;
  private final TraceAssociationsMapper traceAssociationsMapper;
  private final AssociationSearchResultDTOMapper associationSearchResultDTOMapper;

  @GetMapping("/overview")
  public ResponseEntity<List<TraceOverviewDTO>> getTraceOverview(Principal principal) {
    log.debug("Received request to trace overview of user [{}]", principal.getName());
    List<Trace> traces = traceService.lastTracesOf();

    List<TraceOverviewDTO> response =
        traces.stream()
            .map(trace -> traceOverviewMapper.toDTO(trace, traceService.programNameOfTrace(trace)))
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
        "Received request to trace view of user [{}] (page= {}, fileSize= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());
    PagedResult<TraceViewData> tracesResult =
        traceService.getTracesView(keyword, traceFilter, dateFilter, pageCriteria);

    var tracesViewResponse =
        new PagedResponse<>(
            traceViewMapper.toDTOs(tracesResult.content()),
            PageInfoDTO.fromDomain(tracesResult.pageInfo()));

    return ResponseEntity.ok(tracesViewResponse);
  }

  @DeleteMapping()
  public ResponseEntity<String> deleteTraces(
      Principal principal, @RequestBody List<UUID> tracesIds) {
    log.debug("Received request to delete trace [{}] of user [{}]", tracesIds, principal.getName());

    traceService.deleteAllByIds(tracesIds);

    return ResponseEntity.ok("Resource successfully deleted.");
  }

  @GetMapping("/summary")
  public ResponseEntity<TracesSummaryDTO> getTracesSummary(Principal principal) {
    log.debug("Received request to get trace summary of user [{}]", principal.getName());

    TracesSummaryData summary = traceService.getTracesSummary();

    return ResponseEntity.ok(tracesSummaryMapper.toDTO(summary));
  }

  @GetMapping("/{traceId}/detail")
  public ResponseEntity<TraceDetailDTO> getTraceDetail(
      Principal principal, @PathVariable UUID traceId) {
    log.debug(
        "Received request to get trace [{}] detail of user [{}]", traceId, principal.getName());

    TraceDetailData traceDetail = traceService.getTraceDetail(traceId);

    return ResponseEntity.ok(traceDetailMapper.toDTO(traceDetail));
  }

  @GetMapping("/{traceId}/search-for-association/declared-activities")
  public ResponseEntity<PagedResponse<AssociationSearchResultDeclaredActivityDTO>>
      searchDeclaredActivityForAssociation(
          Principal principal,
          @Valid @PathVariable UUID traceId,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) Integer page,
          @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to search declared activity for association with trace [{}] by student"
            + " [{}] (keyword={}, page={}, pageSize={})",
        traceId,
        principal.getName(),
        keyword,
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<AssociationSearchResultData> pagedResult =
        traceService.searchDeclaredActivityForAssociation(traceId, keyword, pageCriteria);

    var response =
        new PagedResponse<>(
            pagedResult.content().stream()
                .map(associationSearchResultDTOMapper::toDeclaredActivityDTO)
                .toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{traceId}/search-for-association/declared-skills")
  public ResponseEntity<PagedResponse<AssociationSearchResultDeclaredSkillIDTO>>
      searchDeclaredSkillForAssociation(
          Principal principal,
          @Valid @PathVariable UUID traceId,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) Integer page,
          @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to search declared skill for association with trace [{}] by student"
            + " [{}] (keyword={}, page={}, pageSize={})",
        traceId,
        principal.getName(),
        keyword,
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<AssociationSearchResultData> pagedResult =
        traceService.searchDeclaredSkillForAssociation(traceId, keyword, pageCriteria);

    var response =
        new PagedResponse<>(
            pagedResult.content().stream()
                .map(associationSearchResultDTOMapper::toDeclaredSkillDTO)
                .toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{traceId}/search-for-association/declared-experiences")
  public ResponseEntity<PagedResponse<AssociationSearchResultDeclaredExperienceDTO>>
      searchDeclaredExperienceForAssociation(
          Principal principal,
          @Valid @PathVariable UUID traceId,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) Integer page,
          @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to search declared experience for association with trace [{}] by student"
            + " [{}] (keyword={}, page={}, pageSize={})",
        traceId,
        principal.getName(),
        keyword,
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<AssociationSearchResultData> pagedResult =
        traceService.searchDeclaredExperienceForAssociation(traceId, keyword, pageCriteria);

    var response =
        new PagedResponse<>(
            pagedResult.content().stream()
                .map(associationSearchResultDTOMapper::toDeclaredExperienceDTO)
                .toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<TracesCreationResponse> createTrace(
      Principal principal, @Valid @RequestBody CreateTraceDTO createTraceDTO) {
    log.debug("Received request to create new trace for user [{}]", principal.getName());
    var trace =
        traceService.createTrace(
            createTraceDTO.title(),
            createTraceDTO.language(),
            createTraceDTO.authorType(),
            createTraceDTO.personalNote(),
            createTraceDTO.iaJustification(),
            createTraceDTO.link());

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
            updateTraceDTO.authorType(),
            updateTraceDTO.personalNote(),
            updateTraceDTO.iaJustification(),
            updateTraceDTO.link());

    return ResponseEntity.ok(traceDetailMapper.toDTO(trace));
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
    return ResponseEntity.ok(traceAssociationsMapper.toDTO(traceAssociations));
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
    return ResponseEntity.ok(traceAssociationsMapper.toDTO(traceAssociations));
  }

  @PostMapping("/{traceId}/associate/declared-experiences")
  public ResponseEntity<TraceAssociationsDTO> associateTraceWithDeclaredExperiences(
      Principal principal,
      @Valid @PathVariable UUID traceId,
      @Valid @RequestBody AssociationsCreationRequest body) {
    log.debug(
        "Received request to associate Trace[{}] with declared experiences [{}] by student [{}]",
        traceId,
        body.idsToAssociate(),
        principal.getName());
    var traceAssociations =
        traceService.associateTraceWithDeclaredExperience(traceId, body.idsToAssociate());
    return ResponseEntity.ok(traceAssociationsMapper.toDTO(traceAssociations));
  }

  @GetMapping("/{traceId}/associations")
  public ResponseEntity<TraceAssociationsDTO> getTraceAssociations(
      Principal principal,
      @PathVariable UUID traceId,
      @RequestParam(required = false, defaultValue = "false") boolean onlyNotCompleted) {
    log.debug(
        "Received request to get Trace[{}] associations by student [{}]"
            + " (onlyNotCompleted= {})",
        traceId,
        principal.getName(),
        onlyNotCompleted);

    var traceAssociations = traceService.getTraceAssociations(traceId, onlyNotCompleted);

    return ResponseEntity.ok(traceAssociationsMapper.toDTO(traceAssociations));
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

  @PostMapping("/locked-declared-activities")
  public ResponseEntity<List<TraceLockedDeclaredActivitiesDTO>> getLockedDeclaredActivities(
      Principal principal, @RequestBody List<UUID> traceIds) {
    log.debug(
        "Received request to get locked declared activities for traces [{}] of user [{}]",
        traceIds,
        principal.getName());

    return ResponseEntity.ok(
        traceLockedDeclaredActivitiesMapper.toDTOs(
            traceService.getLockedDeclaredActivities(traceIds)));
  }
}
