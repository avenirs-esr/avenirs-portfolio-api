package fr.avenirsesr.portfolio.trace.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.CommonLimits.MAX_TRACES_OVERVIEW;
import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.TITLE_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.requireNotBlankAndMaxLength;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.domain.model.shared.File;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.data.*;
import fr.avenirsesr.portfolio.trace.domain.exception.AssociationDoesNotExistException;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.*;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.client.TraceConfigurationClient;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
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
  private final UserRepository userRepository;
  private final StudentProgressRepository studentProgressRepository;
  private final TraceAttachmentRepository traceAttachmentRepository;
  private final DeclaredActivityRepository declaredActivityRepository;
  private final TraceConfigurationClient traceConfigurationClient;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;

  @Override
  public String programNameOfTrace(Trace trace) {
    List<StudentProgress> studentProgresses =
        studentProgressRepository.findStudentProgressesBySkillLevelProgresses(
            trace.getSkillLevels());
    return studentProgresses.stream()
        .filter(sp -> sp.getTrainingPath().getProgram().isAPC())
        .map(sp -> sp.getTrainingPath().getProgram().getName())
        .findAny()
        .orElse(EPortfolioType.LIFE_PROJECT.name());
  }

  @Override
  public List<Trace> lastTracesOf() {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    return traceRepository.findLastsOf(loggedInUser, MAX_TRACES_OVERVIEW);
  }

  @Override
  public List<Trace> getTracesLinkedWithDeclaredSkillProgress(
      DeclaredSkillProgress declaredSkillProgress) {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    List<Trace> traces = traceRepository.linkedWith(declaredSkillProgress);
    traces.forEach(trace -> checkIfUserIsAuthorizedOnTrace(loggedInUser, trace));
    return traces;
  }

  @Override
  public PagedResult<Trace> getTracesView(
      String keyword, TraceFilter filter, DateFilter dateFilter, PageCriteria pageCriteria) {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    PagedResult<Trace> pagedResult =
        traceRepository.findAll(loggedInUser, keyword, filter, dateFilter, pageCriteria);
    return new PagedResult<>(pagedResult.content(), pagedResult.pageInfo());
  }

  @Override
  public void deleteById(UUID id) {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    Trace trace = traceRepository.findById(id).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

    trace.setAmses(new ArrayList<>());
    trace.setSkillLevels(new ArrayList<>());
    trace.setDeclaredSkillProgresses(new ArrayList<>());
    trace.setDeletedAt(Instant.now());

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

    TraceAttachment traceAttachment = getTraceAttachment(trace);

    TraceAssociationsData traceAssociations = getTraceAssociations(trace);
    return new TraceDetailData(
        trace.getId(),
        trace.getTitle(),
        !trace.isUnassociated(),
        programNameOfTrace(trace),
        trace.isGroup(),
        trace.getAiUseJustification().orElse(null),
        trace.getPersonalNote().orElse(null),
        traceAttachment,
        traceAssociations,
        trace.getCreatedAt(),
        trace.getUpdatedAt());
  }

  private TraceAssociationsData getTraceAssociations(Trace trace) {
    List<SkillLevelAssociationData> skillLevelAssociations = new ArrayList<>();
    List<DeclaredSkillAssociationData> declaredSkillAssociations = new ArrayList<>();

    for (SkillLevelProgress skillLevelProgress : trace.getSkillLevels()) {
      var skillLevel = skillLevelProgress.getSkillLevel();
      var skill = skillLevel.getSkill();

      if (skillLevelProgress.getAmses() == null || skillLevelProgress.getAmses().isEmpty()) {
        skillLevelAssociations.add(
            toSkillLevelAssociation(skillLevelProgress, skillLevel, skill, null));
      } else {
        for (AMS ams : skillLevelProgress.getAmses()) {
          skillLevelAssociations.add(
              toSkillLevelAssociation(skillLevelProgress, skillLevel, skill, ams));
        }
      }
    }

    for (DeclaredSkillProgress declaredSkillProgress : trace.getDeclaredSkillProgresses()) {
      declaredSkillAssociations.add(toDeclaredSkillAssociation(declaredSkillProgress));
    }

    return new TraceAssociationsData(
        skillLevelAssociations,
        declaredSkillAssociations,
        List.of()); // todo -> to be implemented with get endpoint
  }

  @Override
  public Trace createTrace(
      UUID traceId,
      UUID userId,
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification) {
    return createTrace(
        traceId,
        userRepository.findById(userId).orElseThrow(),
        title,
        language,
        isGroup,
        personalNote,
        aiJustification);
  }

  @Override
  public Trace createTrace(
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification) {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    return createTrace(
        UUID.randomUUID(), loggedInUser, title, language, isGroup, personalNote, aiJustification);
  }

  private Trace createTrace(
      UUID traceId,
      User user,
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification) {
    requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
    var trace =
        Trace.create(traceId, user, title, language, isGroup, aiJustification, personalNote);

    return traceRepository.save(trace);
  }

  @Override
  public TraceDetailData updateTrace(
      UUID traceId,
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification) {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

    trace.setTitle(title);
    trace.setLanguage(language);
    trace.setGroup(isGroup);
    trace.setPersonalNote(personalNote);
    trace.setAiUseJustification(aiJustification);

    var savedTrace = traceRepository.save(trace);

    TraceAttachment traceAttachment = getTraceAttachment(savedTrace);

    TraceAssociationsData traceAssociations = getTraceAssociations(trace);

    return new TraceDetailData(
        savedTrace.getId(),
        savedTrace.getTitle(),
        !savedTrace.isUnassociated(),
        programNameOfTrace(savedTrace),
        savedTrace.isGroup(),
        savedTrace.getAiUseJustification().orElse(null),
        savedTrace.getPersonalNote().orElse(null),
        traceAttachment,
        traceAssociations,
        savedTrace.getCreatedAt(),
        savedTrace.getUpdatedAt());
  }

  private TraceAttachment getTraceAttachment(Trace trace) {
    return traceAttachmentRepository.findByTrace(trace).stream()
        .filter(File::isActiveVersion)
        .findFirst()
        .orElseThrow(FileNotFoundException::new);
  }

  @Override
  public Optional<LocalDate> getWillBeDeletedAt(Trace trace) {
    var config = traceConfigurationClient.getTraceConfiguration();

    return trace.isUnassociated()
        ? Optional.of(
            trace
                .getCreatedAt()
                .plus(Duration.ofDays(config.maxRemainingDays()))
                .atZone(ZoneId.systemDefault())
                .toLocalDate())
        : Optional.empty();
  }

  @Override
  public TraceAssociationsData associateTraceWithActivities(UUID traceId, List<UUID> activityIds) {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

    var activities = declaredActivityRepository.findAllById(activityIds);

    if (!new HashSet<>(activities.stream().map(DeclaredActivity::getId).toList())
        .containsAll(activityIds)) {
      throw new DeclaredActivityNotFoundException();
    }

    if (!activities.stream().allMatch(a -> a.getStudent().getUser().equals(loggedInUser))) {
      throw new UserNotAuthorizedException();
    }

    List<Association> associations =
        associationService.createAll(
            activityIds.stream()
                .map(
                    activityId ->
                        new AssociationData(
                            activityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE))
                .toList());

    return new TraceAssociationsData(
        List.of(),
        List.of(), // todo replace after refactor of declared skill associations
        associations.stream()
            .map(
                a ->
                    new TraceAssociationsData.DeclaredActivityAssociationData(
                        a.getId(),
                        activities.stream()
                            .filter(activity -> activity.getId().equals(a.getId1()))
                            .findAny()
                            .orElseThrow(DeclaredActivityNotFoundException::new)))
            .toList());
  }

  @Override
  public void unassociateTraces(DeclaredSkillProgress declaredSkillProgress) {
    List<Trace> traces = traceRepository.linkedWith(declaredSkillProgress);
    traces.forEach(trace -> trace.remove(declaredSkillProgress));
    traceRepository.saveAll(traces);
  }

  @Override
  public void unassociateTraces(DeclaredSkillProgress declaredSkillProgress, List<UUID> traceIds) {
    User loggedInUser = loggedInUserService.getLoggedInUser();

    List<Trace> traces = traceRepository.findAllById(traceIds);

    if (traceIds.size() != traces.size()) {
      throw new TraceNotFoundException();
    }

    for (Trace trace : traces) {
      checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

      trace.getDeclaredSkillProgresses().stream()
          .filter(asp -> asp.equals(declaredSkillProgress))
          .findAny()
          .orElseThrow(
              () ->
                  new AssociationDoesNotExistException(
                      trace + " is not associated with " + declaredSkillProgress));

      trace.remove(declaredSkillProgress);
    }

    traceRepository.saveAll(traces);
  }

  private void checkIfUserIsAuthorizedOnTrace(User user, Trace trace) {
    if (!trace.getUser().equals(user)) {
      throw new UserNotAuthorizedException("%s does not own this %s".formatted(user, trace));
    }
  }

  private SkillLevelAssociationData toSkillLevelAssociation(
      SkillLevelProgress skillLevelProgress, SkillLevel skillLevel, Skill skill, AMS ams) {
    AmsAssociationData amsAssociation =
        (ams == null) ? null : new AmsAssociationData(ams.getId(), ams.getTitle(), ams.getStatus());

    return new SkillLevelAssociationData(
        skillLevelProgress.getId(),
        skill.getName(),
        skillLevel.getName(),
        skillLevelProgress.getStatus(),
        amsAssociation);
  }

  private DeclaredSkillAssociationData toDeclaredSkillAssociation(
      DeclaredSkillProgress declaredSkillProgress) {
    var skill = declaredSkillProgress.getSkill();

    return new DeclaredSkillAssociationData(
        declaredSkillProgress.getId(),
        skill.getLibelle(),
        declaredSkillProgress.getLevel(),
        skill.getPathSegments() != null ? skill.getPathSegments() : List.of(),
        skill.getType());
  }
}
