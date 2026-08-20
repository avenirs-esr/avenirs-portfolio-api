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
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.FileDownload;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityAlreadyFinishedException;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.FeedbackService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.exception.AssociationDoesNotExistException;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceAssociationData;
import fr.avenirsesr.portfolio.student.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationData;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
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
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final DeclaredExperienceService declaredExperienceService;
  private final TraceConfigurationClient traceConfigurationClient;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;
  private final AssociationSearchHelper associationSearchHelper;
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
      String keyword, TraceFilter filter, DateFilter dateFilter, PageCriteria pageCriteria) {
    Student loggedInStudent = loggedInUserService.getLoggedInStudent();
    var config = traceConfigurationClient.getTraceConfiguration();

    PagedResult<Trace> pagedResult =
        traceRepository.findAll(loggedInStudent, keyword, filter, dateFilter, pageCriteria);

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
  public TraceAssociationsData getTraceAssociations(UUID traceId, boolean onlyNotCompleted) {
    var studentLoggedIn = loggedInUserService.getLoggedInStudent();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);

    checkIfStudentIsAuthorizedOnTrace(studentLoggedIn, trace);

    var associations =
        associationService.getAllOf(
            trace.getId(), Trace.class, EAssociationType.getAllBy(Trace.class));

    var declaredActivityAssociations =
        associations.stream()
            .filter(a -> a.getAssociationType() == EAssociationType.DECLARED_ACTIVITY_TRACE)
            .toList();

    List<DeclaredActivity> activities;

    if (onlyNotCompleted) {
      activities =
          declaredActivityService.findAllNotCompletedActivitiesByIds(
              declaredActivityAssociations.stream().map(Association::getId1).toList());
    } else {
      activities =
          declaredActivityService.findAllDeclaredActivitiesByIds(
              declaredActivityAssociations.stream().map(Association::getId1).toList());
    }

    var declaredSkillAssociations =
        associations.stream()
            .filter(a -> a.getAssociationType() == EAssociationType.TRACE_DECLARED_SKILL)
            .toList();
    var skills =
        declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
            declaredSkillAssociations.stream().map(Association::getId2).toList());

    var declaredExperienceAssociations =
        associations.stream()
            .filter(a -> a.getAssociationType() == EAssociationType.TRACE_DECLARED_EXPERIENCE)
            .toList();
    var experiences =
        declaredExperienceService.findAllByIds(
            declaredExperienceAssociations.stream().map(Association::getId2).toList());

    var activityStatuses = declaredActivityService.getDeclaredActivityStatus(activities);

    return new TraceAssociationsData(
        declaredActivityAssociations.stream()
            .map(a -> declaredActivityMapper(a, activities, activityStatuses))
            .toList(),
        declaredSkillAssociations.stream().map(a -> declaredSkillMapper(a, skills)).toList(),
        declaredExperienceAssociations.stream()
            .map(a -> declaredExperienceMapper(a, experiences))
            .toList());
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
  public TraceDetailData updateTrace(
      UUID traceId,
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String personalNote,
      String aiJustification) {
    boolean currentValorized =
        traceRepository
            .findById(traceId)
            .map(Trace::isValorized)
            .orElseThrow(TraceNotFoundException::new);
    return updateTrace(
        traceId,
        title,
        language,
        authorType,
        personalNote,
        aiJustification,
        null,
        currentValorized);
  }

  @Override
  public TraceAssociationsData associateTraceWithActivities(UUID traceId, List<UUID> activityIds) {
    Student loggedInStudent = loggedInUserService.getLoggedInStudent();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfStudentIsAuthorizedOnTrace(loggedInStudent, trace);

    var activities = declaredActivityService.findAllDeclaredActivitiesByIds(activityIds);

    if (!new HashSet<>(activities.stream().map(DeclaredActivity::getId).toList())
        .containsAll(activityIds)) {
      throw new DeclaredActivityNotFoundException();
    }

    if (!activities.stream().allMatch(a -> a.getStudent().equals(loggedInStudent))) {
      throw new UserNotAuthorizedException();
    }

    associationService.createAll(
        activityIds.stream()
            .map(
                activityId ->
                    new AssociationData(
                        activityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE))
            .toList());

    return getTraceAssociations(traceId, false);
  }

  private DeclaredActivityAssociationData declaredActivityMapper(
      Association association,
      List<DeclaredActivity> activities,
      Map<DeclaredActivity, EDeclaredActivityStatus> activityStatuses) {

    DeclaredActivity activity =
        activities.stream()
            .filter(a -> a.getId().equals(association.getId1()))
            .findAny()
            .orElseThrow(DeclaredActivityNotFoundException::new);

    return new DeclaredActivityAssociationData(
        association.getId(), activity, activityStatuses.get(activity));
  }

  private DeclaredSkillAssociationData declaredSkillMapper(
      Association association, List<DeclaredSkillProgress> skills) {
    return new DeclaredSkillAssociationData(
        association.getId(),
        skills.stream()
            .filter(skill -> skill.getId().equals(association.getId2()))
            .findAny()
            .orElseThrow(DeclaredSkillProgressNotFoundException::new));
  }

  private DeclaredExperienceAssociationData declaredExperienceMapper(
      Association association, List<DeclaredExperience> experiences) {
    return new DeclaredExperienceAssociationData(
        association.getId(),
        experiences.stream()
            .filter(experience -> experience.getId().equals(association.getId2()))
            .findAny()
            .orElseThrow(DeclaredExperienceNotFoundException::new));
  }

  @Override
  public TraceAssociationsData associateTraceWithDeclaredSkill(UUID traceId, List<UUID> skillIds) {
    Student loggedInStudent = loggedInUserService.getLoggedInStudent();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfStudentIsAuthorizedOnTrace(loggedInStudent, trace);

    var declaredSkills = declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(skillIds);

    if (!new HashSet<>(declaredSkills.stream().map(DeclaredSkillProgress::getId).toList())
        .containsAll(skillIds)) {
      throw new DeclaredSkillProgressNotFoundException();
    }

    if (!declaredSkills.stream().allMatch(a -> a.getStudent().equals(loggedInStudent))) {
      throw new UserNotAuthorizedException();
    }

    associationService.createAll(
        skillIds.stream()
            .map(
                skillId ->
                    new AssociationData(traceId, skillId, EAssociationType.TRACE_DECLARED_SKILL))
            .toList());

    return getTraceAssociations(traceId, false);
  }

  @Override
  public TraceAssociationsData associateTraceWithDeclaredExperience(
      UUID traceId, List<UUID> experienceIds) {
    Student loggedInStudent = loggedInUserService.getLoggedInStudent();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfStudentIsAuthorizedOnTrace(loggedInStudent, trace);

    var declaredExperiences = declaredExperienceService.findAllByIds(experienceIds);

    if (!new HashSet<>(declaredExperiences.stream().map(DeclaredExperience::getId).toList())
        .containsAll(experienceIds)) {
      throw new DeclaredExperienceNotFoundException();
    }

    if (!declaredExperiences.stream().allMatch(a -> a.getStudent().equals(loggedInStudent))) {
      throw new UserNotAuthorizedException();
    }

    associationService.createAll(
        experienceIds.stream()
            .map(
                experienceId ->
                    new AssociationData(
                        traceId, experienceId, EAssociationType.TRACE_DECLARED_EXPERIENCE))
            .toList());

    return getTraceAssociations(traceId, false);
  }

  @Override
  public void unassociate(UUID traceId, List<UUID> associationIds) {
    Student loggedInStudent = loggedInUserService.getLoggedInStudent();
    Trace trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);

    checkIfStudentIsAuthorizedOnTrace(loggedInStudent, trace);

    List<Association> associationList =
        associationService
            .getAllOf(traceId, Trace.class, EAssociationType.getAllBy(Trace.class))
            .stream()
            .filter(association -> associationIds.contains(association.getId()))
            .toList();

    if (!new HashSet<>(associationList.stream().map(Association::getId).toList())
        .containsAll(associationIds)) {
      throw new AssociationDoesNotExistException();
    }

    checkIfDeclaredActivitiesAssociationsAreDeletable(associationList);

    associationService.deleteAllByIds(associationIds);
  }

  private void checkIfDeclaredActivitiesAssociationsAreDeletable(
      List<Association> associationList) {
    List<UUID> declaredActivityIds =
        associationList.stream()
            .filter(
                association ->
                    association.getAssociationType() == EAssociationType.DECLARED_ACTIVITY_TRACE)
            .map(Association::getId1)
            .toList();

    if (declaredActivityService.findAllDeclaredActivitiesByIds(declaredActivityIds).stream()
        .anyMatch(a -> a.getFinishedAt().isPresent())) {
      throw new DeclaredActivityAlreadyFinishedException();
    }
  }

  @Override
  public PagedResult<AssociationSearchResultData> searchTracesForAssociation(
      UUID excludeAssociatedWithElementId,
      EAssociationContextType contextType,
      Boolean isAssociated,
      String keyword,
      PageCriteria pageCriteria) {
    EAssociationType associationType = null;
    if (contextType != null) {
      associationType = getAssociationTypeForTraceCandidate(contextType);
    }

    var traces =
        getTracesView(keyword, new TraceFilter(isAssociated, null, null, null), null, pageCriteria);

    if (contextType == null) {
      return associationSearchHelper.searchForAssociation(
          null,
          null,
          null,
          null,
          traces,
          TraceViewData::id,
          TraceViewData::title,
          null,
          trace -> false);
    }

    return associationSearchHelper.searchForAssociation(
        excludeAssociatedWithElementId,
        contextType.toClass(),
        associationType,
        associationType.idExtractorFor(Trace.class),
        traces,
        TraceViewData::id,
        TraceViewData::title,
        null,
        trace -> false);
  }

  @Override
  public PagedResult<AssociationSearchResultData> searchDeclaredActivityForAssociation(
      UUID traceId, String keyword, PageCriteria pageCriteria) {
    checkTraceOwnership(traceId);
    var associationType = EAssociationType.DECLARED_ACTIVITY_TRACE;
    return associationSearchHelper.searchForAssociation(
        traceId,
        Trace.class,
        associationType,
        associationType.idExtractorFor(DeclaredActivity.class),
        declaredActivityService.searchDeclaredActivity(keyword, pageCriteria),
        AvenirsBaseModel::getId,
        da -> da.getActivity().getTitle(),
        da -> da.getActivity().getThematic().name(),
        da -> da.getFinishedAt().isPresent());
  }

  @Override
  public PagedResult<AssociationSearchResultData> searchDeclaredSkillForAssociation(
      UUID traceId, String keyword, PageCriteria pageCriteria) {
    checkTraceOwnership(traceId);
    var associationType = EAssociationType.TRACE_DECLARED_SKILL;
    return associationSearchHelper.searchForAssociation(
        traceId,
        Trace.class,
        associationType,
        associationType.idExtractorFor(DeclaredSkillProgress.class),
        declaredSkillProgressService.searchDeclaredSkill(keyword, pageCriteria),
        AvenirsBaseModel::getId,
        ds -> ds.getSkill().getLibelle(),
        ds -> ds.getSkill().getType().name(),
        ds -> false);
  }

  @Override
  public PagedResult<AssociationSearchResultData> searchDeclaredExperienceForAssociation(
      UUID traceId, String keyword, PageCriteria pageCriteria) {
    checkTraceOwnership(traceId);
    var associationType = EAssociationType.TRACE_DECLARED_EXPERIENCE;
    return associationSearchHelper.searchForAssociation(
        traceId,
        Trace.class,
        associationType,
        associationType.idExtractorFor(DeclaredExperience.class),
        declaredExperienceService.search(keyword, pageCriteria),
        AvenirsBaseModel::getId,
        DeclaredExperience::getTitle,
        de -> de.getExperienceType() != null ? de.getExperienceType().name() : null,
        de -> false);
  }

  private EAssociationType getAssociationTypeForTraceCandidate(
      EAssociationContextType contextType) {
    return switch (contextType) {
      case DECLARED_ACTIVITY -> EAssociationType.DECLARED_ACTIVITY_TRACE;
      case DECLARED_EXPERIENCE -> EAssociationType.TRACE_DECLARED_EXPERIENCE;
      case TRACE, DECLARED_SKILL -> throw new UnsupportedOperationException();
    };
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

  private void checkTraceOwnership(UUID traceId) {
    var loggedInStudent = loggedInUserService.getLoggedInStudent();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfStudentIsAuthorizedOnTrace(loggedInStudent, trace);
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

  private List<UUID> getAssociatedDeclaredActivityIds(UUID traceId) {
    return getAssociatedDeclaredActivityIdsByTraceId(List.of(traceId))
        .getOrDefault(traceId, List.of())
        .stream()
        .distinct()
        .toList();
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
