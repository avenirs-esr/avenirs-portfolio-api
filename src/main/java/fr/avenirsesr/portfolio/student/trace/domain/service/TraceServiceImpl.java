package fr.avenirsesr.portfolio.student.trace.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.CommonLimits.MAX_TRACES_OVERVIEW;
import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.LINK_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.TITLE_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.requireNotBlankAndMaxLength;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateOptionalTextMaxLength;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateUrl;

import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.common.data.domain.model.*;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.FileDownload;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.FeedbackService;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.trace.domain.data.*;
import fr.avenirsesr.portfolio.student.trace.domain.exception.InvalidTraceTypeException;
import fr.avenirsesr.portfolio.student.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.student.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.model.enums.ETraceAuthorType;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.student.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.client.TraceConfigurationClient;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TraceServiceImpl implements TraceService {
  private final TraceRepository traceRepository;
  private final StudentService studentService;
  private final DeclaredActivityService declaredActivityService;
  private final TraceConfigurationClient traceConfigurationClient;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;
  private final FeedbackService feedbackService;
  private final FileResourceService fileResourceService;

  @Override
  public Trace getTraceById(UUID id) {
    return traceRepository.findById(id).orElseThrow(TraceNotFoundException::new);
  }

  @Override
  public List<Trace> findAllTracesById(List<UUID> ids) {
    return traceRepository.findAllById(ids);
  }

  @Override
  public List<Trace> lastTracesOf() {
    Student loggedInStudent = loggedInUserService.getLoggedInStudent();
    return traceRepository.findLastsOf(loggedInStudent, MAX_TRACES_OVERVIEW);
  }

  @Override
  public PagedResult<TraceViewData> getTracesView(
      String keyword,
      TraceFilter filter,
      DateFilter dateFilter,
      PageCriteria pageCriteria,
      SortCriteria sortCriteria) {
    Student loggedInStudent = loggedInUserService.getLoggedInStudent();
    var config = traceConfigurationClient.getTraceConfiguration();

    PagedResult<Trace> pagedResult =
        traceRepository.findAll(
            loggedInStudent, keyword, filter, dateFilter, pageCriteria, sortCriteria);

    List<Trace> traces = pagedResult.content();

    Map<Trace, Boolean> traceAssociated = traceRepository.isAssociated(traces);

    return new PagedResult<>(
        traces.stream()
            .map(
                trace -> {
                  boolean isAssociated = traceAssociated.getOrDefault(trace, false);

                  return new TraceViewData(
                      trace.getId(),
                      trace.getTitle(),
                      isAssociated,
                      trace.getCreatedAt(),
                      trace.getUpdatedAt(),
                      isAssociated
                          ? Optional.empty()
                          : Optional.of(computeDeletionDateForUnassociatedTrace(trace, config)),
                      trace.getAttachment(),
                      trace.getAuthorType(),
                      trace.getPersonalNote().orElse(null),
                      trace.getAiUseJustification().orElse(null));
                })
            .toList(),
        pagedResult.pageInfo());
  }

  private LocalDate computeDeletionDateForUnassociatedTrace(
      Trace unassociatedTrace, TraceConfiguration configuration) {
    return unassociatedTrace
        .getCreatedAt()
        .plus(Duration.ofDays(configuration.maxRemainingDays()))
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }

  @Override
  public void deleteAllByIds(List<UUID> traceIds) {
    Student loggedInStudent = loggedInUserService.getLoggedInStudent();

    var traces = traceRepository.findAllById(traceIds);

    if (traces.size() != traceIds.stream().distinct().count()) {
      throw new TraceNotFoundException();
    }

    traces.forEach(trace -> checkIfStudentIsAuthorizedOnTrace(loggedInStudent, trace));

    var traceIdsToDelete = traces.stream().map(Trace::getId).toList();

    var declaredActivityIds =
        getAssociatedDeclaredActivityIdsByTraceId(traceIdsToDelete).values().stream()
            .flatMap(Collection::stream)
            .distinct()
            .toList();

    Set<UUID> fileIdsUsedByTraces =
        traces.stream()
            .map(Trace::getAttachment)
            .flatMap(Optional::stream)
            .map(File::getId)
            .collect(Collectors.toSet());

    Set<UUID> protectedFileIds =
        feedbackService.findAttachmentIdsUsedByTraceSnapshots(
            declaredActivityIds, traceIdsToDelete);

    List<UUID> fileIdsToDelete =
        fileIdsUsedByTraces.stream().filter(fileId -> !protectedFileIds.contains(fileId)).toList();

    associationService.deleteAllOf(traceIdsToDelete, Trace.class);

    traceRepository.removeAllFromDatabase(traces);

    deleteAllFilesByIds(fileIdsToDelete);

    log.info("Deleted traces {}", traceIds);
  }

  @Override
  public TracesSummaryData getTracesSummary() {
    Student loggedInStudent = loggedInUserService.getLoggedInStudent();
    List<Trace> associatedTraces = traceRepository.findAll(loggedInStudent, true);
    List<Trace> unassociatedTraces = traceRepository.findAll(loggedInStudent, false);
    TraceConfiguration traceConfiguration = traceConfigurationClient.getTraceConfiguration();

    int criticalCount =
        unassociatedTraces.stream()
            .filter(
                t ->
                    Duration.between(t.getCreatedAt(), Instant.now())
                        .minus(Duration.ofDays(traceConfiguration.maxRemainingDays()))
                        .plus(Duration.ofDays(traceConfiguration.maxRemainingDaysBeforeCritical()))
                        .isPositive())
            .toList()
            .size();

    int warningCount =
        unassociatedTraces.stream()
            .filter(
                t ->
                    Duration.between(t.getCreatedAt(), Instant.now())
                        .minus(Duration.ofDays(traceConfiguration.maxRemainingDays()))
                        .plus(Duration.ofDays(traceConfiguration.maxRemainingDaysBeforeWarning()))
                        .isPositive())
            .toList()
            .size();

    return new TracesSummaryData(
        associatedTraces.size(), unassociatedTraces.size(), warningCount, criticalCount);
  }

  @Override
  public TraceDetailData getTraceDetail(UUID id) {
    Student loggedInStudent = loggedInUserService.getLoggedInStudent();
    Trace trace = traceRepository.findById(id).orElseThrow(TraceNotFoundException::new);
    checkIfStudentIsAuthorizedOnTrace(loggedInStudent, trace);
    var isTraceAssociated = traceRepository.isAssociated(List.of(trace)).get(trace);
    return buildTraceDetailData(trace, isTraceAssociated);
  }

  private TraceDetailData buildTraceDetailData(Trace trace, boolean isAssociated) {
    return new TraceDetailData(
        trace.getId(),
        trace.getTitle(),
        isAssociated,
        trace.getAuthorType(),
        trace.getAiUseJustification().orElse(null),
        trace.getPersonalNote().orElse(null),
        trace.getLink(),
        trace.getAttachment(),
        trace.isValorized(),
        trace.getCreatedAt(),
        trace.getUpdatedAt());
  }

  @Override
  public Trace createTrace(
      UUID traceId,
      UUID studentId,
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String personalNote,
      String aiJustification,
      String link,
      boolean valorized) {
    return createTrace(
        traceId,
        studentService.getStudentById(studentId),
        title,
        language,
        authorType,
        personalNote,
        aiJustification,
        link,
        valorized);
  }

  @Override
  public Trace createTrace(
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String personalNote,
      String aiJustification,
      String link) {
    Student loggedInStudent = loggedInUserService.getLoggedInStudent();
    return createTrace(
        UUID.randomUUID(),
        loggedInStudent,
        title,
        language,
        authorType,
        personalNote,
        aiJustification,
        link,
        false);
  }

  private Trace createTrace(
      UUID traceId,
      Student student,
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String personalNote,
      String aiJustification,
      String link,
      boolean valorized) {
    requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
    validateOptionalTextMaxLength("link", link, LINK_LENGTH);
    validateUrl(link);
    var trace =
        Trace.create(
            traceId,
            student,
            title,
            language,
            authorType,
            aiJustification,
            personalNote,
            link,
            null);
    trace.setValorized(valorized);

    return traceRepository.save(trace);
  }

  @Override
  public TraceDetailData updateTrace(
      UUID traceId,
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String personalNote,
      String aiJustification,
      String link,
      boolean valorized) {
    Student loggedInStudent = loggedInUserService.getLoggedInStudent();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfStudentIsAuthorizedOnTrace(loggedInStudent, trace);

    trace.setTitle(title);
    trace.setLanguage(language);
    trace.setAuthorType(authorType);
    trace.setPersonalNote(personalNote);
    trace.setAiUseJustification(aiJustification);
    trace.setValorized(valorized);

    if (link != null) {
      validateOptionalTextMaxLength("link", link, LINK_LENGTH);
      validateUrl(link);
      trace.setLink(link);
    }

    var savedTrace = traceRepository.save(trace);
    var isTraceAssociated = traceRepository.isAssociated(List.of(savedTrace)).get(savedTrace);
    return buildTraceDetailData(savedTrace, isTraceAssociated);
  }

  @Override
  public List<TraceLockedDeclaredActivitiesData> getLockedDeclaredActivities(List<UUID> traceIds) {
    if (traceIds.isEmpty()) {
      return List.of();
    }

    List<Trace> traces = traceRepository.findAllById(traceIds);

    if (traces.size() != traceIds.size()) {
      throw new TraceNotFoundException();
    }

    traces.forEach(
        trace ->
            checkIfStudentIsAuthorizedOnTrace(loggedInUserService.getLoggedInStudent(), trace));

    Map<UUID, List<UUID>> associatedDeclaredActivityIdsByTraceId =
        getAssociatedDeclaredActivityIdsByTraceId(traceIds);

    List<UUID> declaredActivityIds =
        associatedDeclaredActivityIdsByTraceId.values().stream()
            .flatMap(List::stream)
            .distinct()
            .toList();

    if (declaredActivityIds.isEmpty()) {
      return traces.stream()
          .map(
              trace ->
                  new TraceLockedDeclaredActivitiesData(trace.getId(), trace.getTitle(), List.of()))
          .toList();
    }

    List<DeclaredActivity> declaredActivities =
        declaredActivityService.findAllDeclaredActivitiesByIds(declaredActivityIds);

    Map<DeclaredActivity, EDeclaredActivityStatus> statuses =
        declaredActivityService.getDeclaredActivityStatus(declaredActivities);

    Map<UUID, TraceDeclaredActivityData> lockedDeclaredActivityById =
        statuses.entrySet().stream()
            .filter(entry -> isLocked(entry.getValue()))
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().getId(),
                    entry -> toTraceDeclaredActivityData(entry.getKey(), entry.getValue())));

    return traces.stream()
        .map(
            trace -> {
              List<TraceDeclaredActivityData> lockedActivities =
                  associatedDeclaredActivityIdsByTraceId
                      .getOrDefault(trace.getId(), List.of())
                      .stream()
                      .map(lockedDeclaredActivityById::get)
                      .filter(Objects::nonNull)
                      .toList();

              return new TraceLockedDeclaredActivitiesData(
                  trace.getId(), trace.getTitle(), lockedActivities);
            })
        .toList();
  }

  private void checkIfStudentIsAuthorizedOnTrace(Student student, Trace trace) {
    if (!trace.getStudent().equals(student)) {
      throw new TraceNotFoundException("%s does not own this %s".formatted(student, trace));
    }
  }

  private Map<UUID, List<UUID>> getAssociatedDeclaredActivityIdsByTraceId(List<UUID> traceIds) {
    if (traceIds.isEmpty()) {
      return Map.of();
    }

    return associationService
        .getAllOf(traceIds, Trace.class, List.of(EAssociationType.DECLARED_ACTIVITY_TRACE))
        .stream()
        .collect(
            Collectors.groupingBy(
                Association::getId2, Collectors.mapping(Association::getId1, Collectors.toList())));
  }

  private void deleteAllFilesByIds(List<UUID> fileIds) {
    if (fileIds == null || fileIds.isEmpty()) {
      return;
    }

    fileIds.forEach(fileResourceService::delete);

    log.info("Deleted files {}", fileIds);
  }

  private boolean isLocked(EDeclaredActivityStatus status) {
    return status == EDeclaredActivityStatus.SUBMITTED
        || status == EDeclaredActivityStatus.COMPLETED;
  }

  private TraceDeclaredActivityData toTraceDeclaredActivityData(
      DeclaredActivity declaredActivity, EDeclaredActivityStatus status) {
    return new TraceDeclaredActivityData(
        declaredActivity.getId(), declaredActivity.getActivity().getTitle(), status);
  }

  @Override
  public File uploadAttachment(
      UUID traceId, String fileName, String mimeType, long size, byte[] content) {
    var loggedInStudent = loggedInUserService.getLoggedInStudent();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfStudentIsAuthorizedOnTrace(loggedInStudent, trace);
    if (trace.getLink().isPresent()) {
      throw new InvalidTraceTypeException();
    }

    var file = fileResourceService.upload(fileName, mimeType, size, content, true);
    trace.setAttachment(file);
    traceRepository.save(trace);
    return file;
  }

  @Override
  public FileDownload downloadAttachment(UUID traceId) {
    var loggedInStudent = loggedInUserService.getLoggedInStudent();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfStudentIsAuthorizedOnTrace(loggedInStudent, trace);
    if (trace.getLink().isPresent()) {
      throw new InvalidTraceTypeException();
    }
    var attachment = trace.getAttachment().orElseThrow(InvalidTraceTypeException::new);
    return fileResourceService.download(attachment.getId());
  }
}
