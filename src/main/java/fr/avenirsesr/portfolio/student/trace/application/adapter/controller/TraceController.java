package fr.avenirsesr.portfolio.student.trace.application.adapter.controller;

import static fr.avenirsesr.portfolio.shared.application.adapter.Utils.readBytes;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.file.application.adapter.mapper.FileDtoMapper;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.AssociationsCreationRequest;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultDeclaredActivityDTO;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultDeclaredExperienceDTO;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultDeclaredSkillIDTO;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultTraceDTO;
import fr.avenirsesr.portfolio.student.association.application.adapter.mapper.AssociationSearchResultDTOMapper;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.*;
import fr.avenirsesr.portfolio.student.trace.application.adapter.mapper.*;
import fr.avenirsesr.portfolio.student.trace.application.adapter.response.TracesCreationResponse;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceDetailData;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceViewData;
import fr.avenirsesr.portfolio.student.trace.domain.data.TracesSummaryData;
import fr.avenirsesr.portfolio.student.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/traces")
public class TraceController {
  private final TraceService traceService;
  private final TraceViewMapper traceViewMapper;
  private final TraceOverviewMapper traceOverviewMapper;
  private final TraceDetailMapper traceDetailMapper;
  private final TraceLockedDeclaredActivitiesMapper traceLockedDeclaredActivitiesMapper;
  private final TracesSummaryMapper tracesSummaryMapper;
  private final TraceAssociationsMapper traceAssociationsMapper;
  private final AssociationSearchResultDTOMapper associationSearchResultDTOMapper;
  private final FileDtoMapper fileDtoMapper;

  @PreAuthorize("hasAuthority('trace:list:own')")
  @GetMapping("/overview")
  public ResponseEntity<List<TraceOverviewDTO>> getTraceOverview(Principal principal) {
    log.debug("Received request to trace overview of user [{}]", principal.getName());
    List<Trace> traces = traceService.lastTracesOf();

    List<TraceOverviewDTO> response = traces.stream().map(traceOverviewMapper::toDTO).toList();

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasAuthority('trace:list:own')")
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

  @PreAuthorize("hasAuthority('trace:delete:own')")
  @DeleteMapping()
  public ResponseEntity<String> deleteTraces(
      Principal principal, @RequestBody List<UUID> tracesIds) {
    log.debug("Received request to delete trace [{}] of user [{}]", tracesIds, principal.getName());

    traceService.deleteAllByIds(tracesIds);

    return ResponseEntity.ok("Resource successfully deleted.");
  }

  @PreAuthorize("hasAuthority('trace:list:own')")
  @GetMapping("/summary")
  public ResponseEntity<TracesSummaryDTO> getTracesSummary(Principal principal) {
    log.debug("Received request to get trace summary of user [{}]", principal.getName());

    TracesSummaryData summary = traceService.getTracesSummary();

    return ResponseEntity.ok(tracesSummaryMapper.toDTO(summary));
  }

  @PreAuthorize("hasAuthority('trace:list:own')")
  @GetMapping("/{traceId}/detail")
  public ResponseEntity<TraceDetailDTO> getTraceDetail(
      Principal principal, @PathVariable UUID traceId) {
    log.debug(
        "Received request to get trace [{}] detail of user [{}]", traceId, principal.getName());

    TraceDetailData traceDetail = traceService.getTraceDetail(traceId);

    return ResponseEntity.ok(traceDetailMapper.toDTO(traceDetail));
  }

  @PreAuthorize("hasAuthority('trace:association:manage:own')")
  @GetMapping("/search-for-association")
  public ResponseEntity<PagedResponse<AssociationSearchResultTraceDTO>> searchTracesForAssociation(
      Principal principal,
      @RequestParam(required = false) UUID excludeAssociatedWithElementId,
      @Parameter(schema = @Schema(ref = "#/components/schemas/EAssociationContextType"))
          @RequestParam(required = false)
          EAssociationContextType contextType,
      @RequestParam(required = false) Boolean isAssociated,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to search traces for association (contextType={},"
            + " excludeAssociatedWithElementId={}) by student [{}] (keyword={}, page={},"
            + " pageSize={})",
        contextType,
        excludeAssociatedWithElementId,
        principal.getName(),
        keyword,
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<AssociationSearchResultData> pagedResult =
        traceService.searchTracesForAssociation(
            excludeAssociatedWithElementId, contextType, isAssociated, keyword, pageCriteria);

    return ResponseEntity.ok(
        new PagedResponse<>(
            pagedResult.content().stream()
                .map(associationSearchResultDTOMapper::toTraceDTO)
                .toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo())));
  }

  @PreAuthorize("hasAuthority('trace:association:manage:own')")
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

  @PreAuthorize("hasAuthority('trace:association:manage:own')")
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

  @PreAuthorize("hasAuthority('trace:association:manage:own')")
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

  @PreAuthorize("hasAuthority('trace:create:own')")
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

  @PreAuthorize("hasAuthority('trace:update:own')")
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
            updateTraceDTO.link(),
            updateTraceDTO.valorized());

    return ResponseEntity.ok(traceDetailMapper.toDTO(trace));
  }

  @PreAuthorize("hasAuthority('trace:association:manage:own')")
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

  @PreAuthorize("hasAuthority('trace:association:manage:own')")
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

  @PreAuthorize("hasAuthority('trace:association:manage:own')")
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

  @PreAuthorize("hasAuthority('trace:list:own')")
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

  @PreAuthorize("hasAuthority('trace:association:manage:own')")
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

  @PreAuthorize("hasAuthority('trace:list:own')")
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

  @PreAuthorize("hasAuthority('trace:update:own')")
  @PostMapping(value = "/{traceId}/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileDTO> uploadAttachment(
      Principal principal, @PathVariable UUID traceId, @RequestParam("file") MultipartFile file) {
    log.debug(
        "Received request to upload attachment for trace [{}] by user [{}]",
        traceId,
        principal.getName());
    var uploaded =
        traceService.uploadAttachment(
            traceId,
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            readBytes(file));
    return ResponseEntity.status(HttpStatus.CREATED).body(fileDtoMapper.fromDomain(uploaded));
  }

  @PreAuthorize("hasAuthority('trace:list:own')")
  @GetMapping(
      value = "/{traceId}/attachment/download",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<byte[]> downloadAttachment(
      Principal principal, @PathVariable UUID traceId) {
    log.debug(
        "Received request to download attachment of trace [{}] by user [{}]",
        traceId,
        principal.getName());
    var downloadedFile = traceService.downloadAttachment(traceId);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + downloadedFile.fileName() + "\"")
        .body(downloadedFile.content());
  }
}
