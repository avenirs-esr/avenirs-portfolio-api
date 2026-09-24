package fr.avenirsesr.portfolio.student.trace.application.adapter.controller;

import static fr.avenirsesr.portfolio.shared.application.adapter.Utils.readBytes;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.file.application.adapter.mapper.FileDtoMapper;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.*;
import fr.avenirsesr.portfolio.student.trace.application.adapter.mapper.*;
import fr.avenirsesr.portfolio.student.trace.application.adapter.response.TracesCreationResponse;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceDetailData;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceViewData;
import fr.avenirsesr.portfolio.student.trace.domain.data.TracesSummaryData;
import fr.avenirsesr.portfolio.student.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
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
        traceService.getTracesView(keyword, traceFilter, dateFilter, pageCriteria, null);

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
