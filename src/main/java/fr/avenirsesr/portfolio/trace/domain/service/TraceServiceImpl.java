package fr.avenirsesr.portfolio.trace.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.CommonLimits.MAX_TRACES_OVERVIEW;
import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.LINK_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.TITLE_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.requireNotBlankAndMaxLength;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateOptionalTextMaxLength;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateUrl;

import fr.avenirsesr.portfolio.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.association.domain.exception.AssociationDoesNotExistException;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.common.data.domain.model.*;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityAlreadyFinishedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.FeedbackService;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.data.DeclaredExperienceAssociationData;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.trace.domain.data.*;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.*;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.client.TraceConfigurationClient;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
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
  private final UserService userService;
  private final DeclaredActivityService declaredActivityService;
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final DeclaredExperienceService declaredExperienceService;
  private final TraceConfigurationClient traceConfigurationClient;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;
  private final AssociationSearchHelper associationSearchHelper;
  private final FeedbackService feedbackService;
  private final FileRepository fileRepository;

  @Override
  public Trace getTraceById(UUID id) {
    return traceRepository.findById(id).orElseThrow(TraceNotFoundException::new);
  }

  @Override
  public List<Trace> findAllTracesById(List<UUID> ids) {
    return traceRepository.findAllById(ids);
  }

  @Override
  public String programNameOfTrace(Trace trace) {
    return EPortfolioType.LIFE_PROJECT.name();
  }

  @Override
  public List<Trace> lastTracesOf() {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    return traceRepository.findLastsOf(loggedInUser, MAX_TRACES_OVERVIEW);
  }

  @Override
  public PagedResult<TraceViewData> getTracesView(
      String keyword, TraceFilter filter, DateFilter dateFilter, PageCriteria pageCriteria) {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    var config = traceConfigurationClient.getTraceConfiguration();

    PagedResult<Trace> pagedResult =
        traceRepository.findAll(loggedInUser, keyword, filter, dateFilter, pageCriteria);

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
                          : Optional.of(computeDeletionDateForUnassociatedTrace(trace, config)));
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
    User loggedInUser = loggedInUserService.getLoggedInUser();

    var traces = traceRepository.findAllById(traceIds);

    if (traces.size() != traceIds.stream().distinct().count()) {
      throw new TraceNotFoundException();
    }

    traces.forEach(trace -> checkIfUserIsAuthorizedOnTrace(loggedInUser, trace));

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
    User loggedInUser = loggedInUserService.getLoggedInUser();
    List<Trace> associatedTraces = traceRepository.findAll(loggedInUser, true);
    List<Trace> unassociatedTraces = traceRepository.findAll(loggedInUser, false);
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
    User loggedInUser = loggedInUserService.getLoggedInUser();
    Trace trace = traceRepository.findById(id).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);
    var isTraceAssociated = traceRepository.isAssociated(List.of(trace)).get(trace);
    return buildTraceDetailData(trace, isTraceAssociated);
  }

  private TraceDetailData buildTraceDetailData(Trace trace, boolean isAssociated) {
    return new TraceDetailData(
        trace.getId(),
        trace.getTitle(),
        isAssociated,
        programNameOfTrace(trace),
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
    var userLoggedIn = loggedInUserService.getLoggedInUser();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);

    checkIfUserIsAuthorizedOnTrace(userLoggedIn, trace);

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
      UUID userId,
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String personalNote,
      String aiJustification,
      String link,
      boolean valorized) {
    return createTrace(
        traceId,
        userService.getUser(userId),
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
    User loggedInUser = loggedInUserService.getLoggedInUser();
    return createTrace(
        UUID.randomUUID(),
        loggedInUser,
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
      User user,
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
            traceId, user, title, language, authorType, aiJustification, personalNote, link, null);
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
    User loggedInUser = loggedInUserService.getLoggedInUser();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

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
    User loggedInUser = loggedInUserService.getLoggedInUser();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

    var activities = declaredActivityService.findAllDeclaredActivitiesByIds(activityIds);

    if (!new HashSet<>(activities.stream().map(DeclaredActivity::getId).toList())
        .containsAll(activityIds)) {
      throw new DeclaredActivityNotFoundException();
    }

    if (!activities.stream().allMatch(a -> a.getStudent().getUser().equals(loggedInUser))) {
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
    User loggedInUser = loggedInUserService.getLoggedInUser();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

    var declaredSkills = declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(skillIds);

    if (!new HashSet<>(declaredSkills.stream().map(DeclaredSkillProgress::getId).toList())
        .containsAll(skillIds)) {
      throw new DeclaredSkillProgressNotFoundException();
    }

    if (!declaredSkills.stream().allMatch(a -> a.getStudent().getUser().equals(loggedInUser))) {
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
    User loggedInUser = loggedInUserService.getLoggedInUser();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

    var declaredExperiences = declaredExperienceService.findAllByIds(experienceIds);

    if (!new HashSet<>(declaredExperiences.stream().map(DeclaredExperience::getId).toList())
        .containsAll(experienceIds)) {
      throw new DeclaredExperienceNotFoundException();
    }

    if (!declaredExperiences.stream()
        .allMatch(a -> a.getStudent().getUser().equals(loggedInUser))) {
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
    User loggedInUser = loggedInUserService.getLoggedInUser();
    Trace trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);

    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

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
        trace -> checkIfUserIsAuthorizedOnTrace(loggedInUserService.getLoggedInUser(), trace));

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

  private void checkIfUserIsAuthorizedOnTrace(User user, Trace trace) {
    if (!trace.getUser().equals(user)) {
      throw new UserNotAuthorizedException("%s does not own this %s".formatted(user, trace));
    }
  }

  private void checkTraceOwnership(UUID traceId) {
    var loggedInUser = loggedInUserService.getLoggedInUser();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);
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

    var files = fileRepository.findAllById(fileIds);

    if (files.size() != fileIds.stream().distinct().count()) {
      throw new FileNotFoundException();
    }

    fileRepository.removeAllFromDatabase(files);

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
}
