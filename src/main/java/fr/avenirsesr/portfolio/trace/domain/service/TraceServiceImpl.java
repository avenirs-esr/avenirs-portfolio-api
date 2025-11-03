package fr.avenirsesr.portfolio.trace.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
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
import fr.avenirsesr.portfolio.student.progress.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
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
  private final AdditionalSkillProgressRepository additionalSkillProgressRepository;
  private final AMSRepository amsRepository;
  private final SkillLevelProgressRepository skillLevelProgressRepository;
  private final TraceAttachmentRepository traceAttachmentRepository;
  private final StudentService studentService;
  private final TraceConfigurationClient traceConfigurationClient;

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
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    return traceRepository.findLastsOf(loggedInUser, MAX_TRACES_OVERVIEW);
  }

  @Override
  public List<Trace> getTracesLinkedWithAdditionalSkillProgress(
      User user, AdditionalSkillProgress additionalSkillProgress) {
    List<Trace> traces = traceRepository.linkedWith(additionalSkillProgress);
    traces.forEach(trace -> checkIfUserIsAuthorizedOnTrace(user, trace));
    return traces;
  }

  @Override
  public PagedResult<Trace> getTracesView(
      String keyword, TraceFilter filter, DateFilter dateFilter, PageCriteria pageCriteria) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    PagedResult<Trace> pagedResult =
        traceRepository.findAll(loggedInUser, keyword, filter, dateFilter, pageCriteria);
    return new PagedResult<>(pagedResult.content(), pagedResult.pageInfo());
  }

  @Override
  public void deleteById(UUID id) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    Trace trace = traceRepository.findById(id).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

    trace.setAmses(new ArrayList<>());
    trace.setSkillLevels(new ArrayList<>());
    trace.setAdditionalSkillProgresses(new ArrayList<>());
    trace.setDeletedAt(Instant.now());

    traceRepository.save(trace);
    log.info("Deleted trace {}", trace);
  }

  @Override
  public TracesSummaryData getTracesSummary() {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
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
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
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
    List<AdditionalSkillAssociationData> additionalSkillAssociations = new ArrayList<>();

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

    for (AdditionalSkillProgress additionalSkillProgress : trace.getAdditionalSkillProgresses()) {
      additionalSkillAssociations.add(toAdditionalSkillAssociation(additionalSkillProgress));
    }

    return new TraceAssociationsData(skillLevelAssociations, additionalSkillAssociations);
  }

  @Override
  public Trace createTrace(
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
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
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
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
      List<UUID> additionalSkillProgressIds) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

    var student = studentService.getStudentById(loggedInUser.getId());
    associateAMS(student, trace, amsIds);
    associateSkillLevels(student, trace, skillLevelIds);
    associateAdditionalSkillProgress(student, trace, additionalSkillProgressIds);

    traceRepository.save(trace);
    log.info(
        "Trace {} successfully associated with amses : {} - skill level progress {} - additional"
            + " skill progress {}",
        trace,
        amsIds,
        skillLevelIds,
        additionalSkillProgressIds);
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

  private void associateAdditionalSkillProgress(
      Student student, Trace trace, List<UUID> additionalSkillProgressIds) {
    if (additionalSkillProgressIds.stream()
        .anyMatch(
            id ->
                trace.getAdditionalSkillProgresses().stream()
                    .map(AdditionalSkillProgress::getId)
                    .toList()
                    .contains(id))) {
      log.error(
          "{} tried to associate trace with an additional skill that is already associated. IDS :"
              + " {}",
          student,
          additionalSkillProgressIds);
      throw new UserNotAuthorizedException();
    }

    var studentAdditionalSkillProgress =
        additionalSkillProgressRepository.findAllByStudent(student);
    additionalSkillProgressIds.forEach(
        additionalSkillProgressId -> {
          var additionalSkillProgress =
              studentAdditionalSkillProgress.stream()
                  .filter(s -> additionalSkillProgressId.equals(s.getId()))
                  .findAny()
                  .orElseThrow(UserNotAuthorizedException::new);
          trace.add(additionalSkillProgress);
        });
  }

  @Override
  public void unassociateTrace(
      UUID traceId,
      List<UUID> amsIds,
      List<UUID> skillLevelIds,
      List<UUID> additionalSkillProgressIds) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

    var student = studentService.getStudentById(loggedInUser.getId());
    unassociateAdditionalSkillProgress(student, trace, additionalSkillProgressIds);
    unassociateAms(student, trace, amsIds);
    unassociateSkillLevel(student, trace, skillLevelIds);

    traceRepository.save(trace);
    log.info(
        "Trace {} successfully unassociated with amses : {} - skill level progress {} - additional"
            + " skill progress {}",
        trace,
        amsIds,
        skillLevelIds,
        additionalSkillProgressIds);
  }

  @Override
  public void unassociateTraces(
      AdditionalSkillProgress additionalSkillProgress, List<UUID> traceIds) {
    User loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);

    List<Trace> traces = traceRepository.findAllById(traceIds);

    if (traceIds.size() != traces.size()) {
      throw new TraceNotFoundException();
    }

    for (Trace trace : traces) {
      checkIfUserIsAuthorizedOnTrace(loggedInUser, trace);

      trace.getAdditionalSkillProgresses().stream()
          .filter(asp -> asp.equals(additionalSkillProgress))
          .findAny()
          .orElseThrow(
              () ->
                  new AssociationDoesNotExistException(
                      trace + " is not associated with " + additionalSkillProgress));

      trace.remove(additionalSkillProgress);
    }

    traceRepository.saveAll(traces);
  }

  private void unassociateAdditionalSkillProgress(
      Student student, Trace trace, List<UUID> additionalSkillProgressIds) {
    if (!new HashSet<>(
            trace.getAdditionalSkillProgresses().stream()
                .map(AdditionalSkillProgress::getId)
                .toList())
        .containsAll(additionalSkillProgressIds)) {
      log.error(
          "{} tried to unassociate trace with an additional skill that is not associated. ids : "
              + " {}",
          student,
          additionalSkillProgressIds);
      throw new UserNotAuthorizedException();
    }
    var studentAdditionalSkillProgress =
        additionalSkillProgressRepository.findAllByStudent(student);
    additionalSkillProgressIds.forEach(
        id -> {
          var progress =
              studentAdditionalSkillProgress.stream()
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

  private AdditionalSkillAssociationData toAdditionalSkillAssociation(
      AdditionalSkillProgress additionalSkillProgress) {
    var skill = additionalSkillProgress.getSkill();

    return new AdditionalSkillAssociationData(
        additionalSkillProgress.getId(),
        skill.getLibelle(),
        additionalSkillProgress.getLevel(),
        skill.getCategoryPath().stream().map(AdditionalSkillCategory::getLibelle).toList(),
        skill.getType());
  }
}
