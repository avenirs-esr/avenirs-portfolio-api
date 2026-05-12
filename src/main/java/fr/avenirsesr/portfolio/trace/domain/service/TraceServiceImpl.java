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
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.domain.model.shared.File;
import fr.avenirsesr.portfolio.file.domain.port.input.TraceAttachmentService;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityAlreadyFinishedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
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
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.client.TraceConfigurationClient;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TraceServiceImpl implements TraceService {
  private final TraceRepository traceRepository;
  private final UserService userService;
  private final TraceAttachmentService traceAttachmentService;
  private final DeclaredActivityService declaredActivityService;
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final DeclaredExperienceService declaredExperienceService;
  private final TraceConfigurationClient traceConfigurationClient;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;
  private final AssociationSearchHelper associationSearchHelper;

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
    Map<Trace, Boolean> traceAssociated = traceRepository.isAssociated(pagedResult.content());
    return new PagedResult<>(
        pagedResult.content().stream()
            .map(
                trace ->
                    new TraceViewData(
                        trace.getId(),
                        trace.getTitle(),
                        traceAssociated.get(trace),
                        trace.getCreatedAt(),
                        trace.getUpdatedAt(),
                        traceAssociated.get(trace)
                            ? Optional.empty()
                            : Optional.of(computeDeletionDateForUnassociatedTrace(trace, config))))
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
  public void deleteById(UUID id) {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    Trace trace = traceRepository.findById(id).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

    trace.setDeletedAt(Instant.now());

    associationService.deleteAllOf(List.of(trace.getId()), Trace.class);

    traceRepository.save(trace);
    log.info("Deleted trace {}", trace);
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
    Optional<TraceAttachment> traceAttachment =
        trace.getLink().isPresent() ? Optional.empty() : Optional.of(getTraceAttachment(trace));

    return new TraceDetailData(
        trace.getId(),
        trace.getTitle(),
        isAssociated,
        programNameOfTrace(trace),
        trace.isGroup(),
        trace.getAiUseJustification().orElse(null),
        trace.getPersonalNote().orElse(null),
        trace.getLink(),
        traceAttachment,
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

    return new TraceAssociationsData(
        declaredActivityAssociations.stream()
            .map(a -> declaredActivityMapper(a, activities))
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
      boolean isGroup,
      String personalNote,
      String aiJustification,
      String link) {
    return createTrace(
        traceId,
        userService.getUser(userId),
        title,
        language,
        isGroup,
        personalNote,
        aiJustification,
        link);
  }

  @Override
  public Trace createTrace(
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification,
      String link) {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    return createTrace(
        UUID.randomUUID(),
        loggedInUser,
        title,
        language,
        isGroup,
        personalNote,
        aiJustification,
        link);
  }

  private Trace createTrace(
      UUID traceId,
      User user,
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification,
      String link) {
    requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
    validateOptionalTextMaxLength("link", link, LINK_LENGTH);
    validateUrl(link);
    var trace =
        Trace.create(traceId, user, title, language, isGroup, aiJustification, personalNote, link);

    return traceRepository.save(trace);
  }

  @Override
  public TraceDetailData updateTrace(
      UUID traceId,
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification,
      String link) {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

    trace.setTitle(title);
    trace.setLanguage(language);
    trace.setGroup(isGroup);
    trace.setPersonalNote(personalNote);
    trace.setAiUseJustification(aiJustification);

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
      boolean isGroup,
      String personalNote,
      String aiJustification) {
    return updateTrace(traceId, title, language, isGroup, personalNote, aiJustification, null);
  }

  private TraceAttachment getTraceAttachment(Trace trace) {
    return traceAttachmentService.findByTrace(trace).stream()
        .filter(File::isActiveVersion)
        .findFirst()
        .orElseThrow(FileNotFoundException::new);
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
      Association association, List<DeclaredActivity> activities) {
    return new DeclaredActivityAssociationData(
        association.getId(),
        activities.stream()
            .filter(activity -> activity.getId().equals(association.getId1()))
            .findAny()
            .orElseThrow(DeclaredActivityNotFoundException::new));
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
        de -> de.getExperienceType().name(),
        de -> false);
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
}
