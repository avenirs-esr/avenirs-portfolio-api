package fr.avenirsesr.portfolio.trace.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.port.input.TraceConfigurationService;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.TracesSummary;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
  private final TraceConfigurationService traceConfigurationService;

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
  public List<Trace> lastTracesOf(User user) {
    return traceRepository.findLastsOf(user, MAX_TRACES_OVERVIEW);
  }

  @Override
  public PagedResult<Trace> getTracesView(
      User user, PageCriteria pageCriteria, ETraceStatus status, String keyword) {
    PagedResult<Trace> pagedResult = traceRepository.findAll(user, pageCriteria, status, keyword);
    return new PagedResult<>(pagedResult.content(), pagedResult.pageInfo());
  }

  @Override
  public void deleteById(User user, UUID id) {
    Trace trace = traceRepository.findById(id).orElseThrow(TraceNotFoundException::new);

    if (!trace.getUser().getId().equals(user.getId())) {
      throw new UserNotAuthorizedException();
    }

    trace.setAmses(new ArrayList<>());
    trace.setSkillLevels(new ArrayList<>());
    trace.setAdditionalSkillProgresses(new ArrayList<>());
    trace.setDeletedAt(Instant.now());

    traceRepository.save(trace);
    log.info("Deleted trace {}", trace);
  }

  @Override
  public TracesSummary getTracesSummary(User user) {
    List<Trace> associatedTraces = traceRepository.findAll(user, ETraceStatus.ASSOCIATED);
    List<Trace> unassociatedTraces = traceRepository.findAll(user, ETraceStatus.UNASSOCIATED);
    TraceConfiguration traceConfiguration = traceConfigurationService.getTraceConfiguration();

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

    return new TracesSummary(
        associatedTraces.size(), unassociatedTraces.size(), warningCount, criticalCount);
  }

  @Override
  public Trace createTrace(
      User user,
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification) {
    var trace =
        Trace.create(
            UUID.randomUUID(), user, title, language, isGroup, aiJustification, personalNote);

    traceRepository.save(trace);
    return trace;
  }

  @Override
  public Optional<LocalDate> getWillBeDeletedAt(Trace trace) {
    var config = traceConfigurationService.getTraceConfiguration();

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
      User user,
      UUID traceId,
      List<UUID> amsIds,
      List<UUID> skillLevelIds,
      List<UUID> additionalSkillProgressIds) {
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);

    if (!trace.getUser().getId().equals(user.getId())) {
      throw new UserNotAuthorizedException();
    }

    var student = user.toStudent();
    associateAMS(student, trace, amsIds);
    associateSkillLevels(student, trace, skillLevelIds);
    associateAdditionalSkillProgress(student, trace, additionalSkillProgressIds);

    traceRepository.save(trace);
    log.info(
        "Trace {} successfully associated with amses : {} - skill level progress {} - additonal"
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
}
