package fr.avenirsesr.portfolio.trace.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.TITLE_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.requireNotBlankAndMaxLength;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.requireNotNull;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.domain.model.shared.File;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.output.repository.DeclaredSkillProgressRepository;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.data.*;
import fr.avenirsesr.portfolio.trace.domain.exception.AssociationDoesNotExistException;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.*;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.client.TraceConfigurationClient;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
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
  private static final int MAX_TRACES_OVERVIEW = 3;
  private final TraceRepository traceRepository;
  private final StudentProgressRepository studentProgressRepository;
  private final DeclaredSkillProgressRepository declaredSkillProgressRepository;
  private final AMSRepository amsRepository;
  private final SkillLevelProgressRepository skillLevelProgressRepository;
  private final TraceAttachmentRepository traceAttachmentRepository;
  private final StudentService studentService;
  private final TraceConfigurationClient traceConfigurationClient;
  private final LoggedInUserService loggedInUserService;

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
      User user, DeclaredSkillProgress declaredSkillProgress) {
    List<Trace> traces = traceRepository.linkedWith(declaredSkillProgress);
    traces.forEach(trace -> checkIfUserIsAuthorizedOnTrace(user, trace));
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

    return new TraceAssociationsData(skillLevelAssociations, declaredSkillAssociations);
  }

  @Override
  public Trace createTrace(
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification) {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
    var trace =
        Trace.create(
            UUID.randomUUID(),
            loggedInUser,
            title,
            language,
            isGroup,
            aiJustification,
            personalNote);

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
  public void associateTrace(
      UUID traceId,
      List<UUID> amsIds,
      List<UUID> skillLevelIds,
      List<UUID> declaredSkillProgressIds) {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    requireNotNull("traceId", traceId);
    requireNotNull("amsIds", amsIds);
    requireNotNull("skillLevelIds", skillLevelIds);
    requireNotNull("declaredSkillProgressIds", declaredSkillProgressIds);
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

    var student = studentService.getStudentById(loggedInUser.getId());
    associateAMS(student, trace, amsIds);
    associateSkillLevels(student, trace, skillLevelIds);
    associateDeclaredSkillProgress(student, trace, declaredSkillProgressIds);

    traceRepository.save(trace);
    log.info(
        "Trace {} successfully associated with amses : {} - skill level progress {} - declared"
            + " skill progress {}",
        trace,
        amsIds,
        skillLevelIds,
        declaredSkillProgressIds);
  }

  private void associateAMS(Student student, Trace trace, List<UUID> amsIds) {
    if (amsIds.stream()
        .anyMatch(id -> trace.getAmses().stream().map(AMS::getId).toList().contains(id))) {
      log.error(
          "{} tried to associate trace with an ams that is already associated. IDS : {}",
          student,
          amsIds);
      throw new UserNotAuthorizedException();
    }

    var studentAmses = amsRepository.findAllByStudent(student);
    amsIds.forEach(
        amsId -> {
          var ams =
              studentAmses.stream()
                  .filter(a -> amsId.equals(a.getId()))
                  .findAny()
                  .orElseThrow(UserNotAuthorizedException::new);
          trace.add(ams);
        });
  }

  private void associateSkillLevels(Student student, Trace trace, List<UUID> skillLevelIds) {
    if (skillLevelIds.stream()
        .anyMatch(
            id ->
                trace.getSkillLevels().stream()
                    .map(SkillLevelProgress::getId)
                    .toList()
                    .contains(id))) {
      log.error(
          "{} tried to associate trace with a skill levels that is already associated. IDS : {}",
          student,
          skillLevelIds);
      throw new UserNotAuthorizedException();
    }

    var studentSkillLevelProgresses = skillLevelProgressRepository.findAllByStudent(student);
    skillLevelIds.forEach(
        skillLevelId -> {
          var skillLevelProgress =
              studentSkillLevelProgresses.stream()
                  .filter(s -> skillLevelId.equals(s.getId()))
                  .findAny()
                  .orElseThrow(UserNotAuthorizedException::new);

          trace.add(skillLevelProgress);
        });
  }

  private void associateDeclaredSkillProgress(
      Student student, Trace trace, List<UUID> declaredSkillProgressIds) {
    if (declaredSkillProgressIds.stream()
        .anyMatch(
            id ->
                trace.getDeclaredSkillProgresses().stream()
                    .map(DeclaredSkillProgress::getId)
                    .toList()
                    .contains(id))) {
      log.error(
          "{} tried to associate trace with an declared skill that is already associated. IDS :"
              + " {}",
          student,
          declaredSkillProgressIds);
      throw new UserNotAuthorizedException();
    }

    var studentDeclaredSkillProgress = declaredSkillProgressRepository.findAllByStudent(student);
    declaredSkillProgressIds.forEach(
        declaredSkillProgressId -> {
          var declaredSkillProgress =
              studentDeclaredSkillProgress.stream()
                  .filter(s -> declaredSkillProgressId.equals(s.getId()))
                  .findAny()
                  .orElseThrow(UserNotAuthorizedException::new);
          trace.add(declaredSkillProgress);
        });
  }

  @Override
  public void unassociateTrace(
      UUID traceId,
      List<UUID> amsIds,
      List<UUID> skillLevelIds,
      List<UUID> declaredSkillProgressIds) {
    User loggedInUser = loggedInUserService.getLoggedInUser();
    requireNotNull("traceId", traceId);
    requireNotNull("amsIds", amsIds);
    requireNotNull("skillLevelIds", skillLevelIds);
    requireNotNull("declaredSkillProgressIds", declaredSkillProgressIds);
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

    var student = studentService.getStudentById(loggedInUser.getId());
    unassociateDeclaredSkillProgress(student, trace, declaredSkillProgressIds);
    unassociateAms(student, trace, amsIds);
    unassociateSkillLevel(student, trace, skillLevelIds);

    traceRepository.save(trace);
    log.info(
        "Trace {} successfully unassociated with amses : {} - skill level progress {} - declared"
            + " skill progress {}",
        trace,
        amsIds,
        skillLevelIds,
        declaredSkillProgressIds);
  }

  @Override
  public void unassociateTraces(DeclaredSkillProgress declaredSkillProgress, List<UUID> traceIds) {
    User loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);

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

  private void unassociateDeclaredSkillProgress(
      Student student, Trace trace, List<UUID> declaredSkillProgressIds) {
    if (!new HashSet<>(
            trace.getDeclaredSkillProgresses().stream().map(DeclaredSkillProgress::getId).toList())
        .containsAll(declaredSkillProgressIds)) {
      log.error(
          "{} tried to unassociate trace with an declared skill that is not associated. ids : "
              + " {}",
          student,
          declaredSkillProgressIds);
      throw new UserNotAuthorizedException();
    }
    var studentDeclaredSkillProgress = declaredSkillProgressRepository.findAllByStudent(student);
    declaredSkillProgressIds.forEach(
        id -> {
          var progress =
              studentDeclaredSkillProgress.stream()
                  .filter(s -> id.equals(s.getId()))
                  .findAny()
                  .orElseThrow(UserNotAuthorizedException::new);
          trace.remove(progress);
        });
  }

  private void unassociateSkillLevel(Student student, Trace trace, List<UUID> skillLevelIds) {
    if (!new HashSet<>(trace.getSkillLevels().stream().map(SkillLevelProgress::getId).toList())
        .containsAll(skillLevelIds)) {
      log.error(
          "{} tried to unassociate trace with a skill level that is not associated. ids :  {}",
          student,
          skillLevelIds);
      throw new UserNotAuthorizedException();
    }
    var studentSkillLevelProgress = skillLevelProgressRepository.findAllByStudent(student);
    skillLevelIds.forEach(
        id -> {
          var progress =
              studentSkillLevelProgress.stream()
                  .filter(s -> id.equals(s.getId()))
                  .findAny()
                  .orElseThrow(UserNotAuthorizedException::new);
          trace.remove(progress);
        });
  }

  private void unassociateAms(Student student, Trace trace, List<UUID> amsIds) {
    if (!new HashSet<>(trace.getAmses().stream().map(AMS::getId).toList()).containsAll(amsIds)) {
      log.error(
          "{} tried to unassociate trace with an ams that is not associated. ids :  {}",
          student,
          amsIds);
      throw new UserNotAuthorizedException();
    }
    var studentAmses = amsRepository.findAllByStudent(student);
    amsIds.forEach(
        id -> {
          var ams =
              studentAmses.stream()
                  .filter(s -> id.equals(s.getId()))
                  .findAny()
                  .orElseThrow(UserNotAuthorizedException::new);
          trace.remove(ams);
        });
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
