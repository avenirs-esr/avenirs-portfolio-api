package fr.avenirsesr.portfolio.student.progress.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotFoundException;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.DuplicateAdditionalSkillException;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.InvalidDescriptionException;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.student.progress.domain.data.AdditionalSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillLevelProgressWithTraceCountData;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.progress.domain.exception.AdditionalSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.progress.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.data.TraceWithProjectNameData;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class StudentProgressServiceImpl implements StudentProgressService {
  public static final int DESCRIPTION_LENGTH_MAX = 400;
  private static final int MAX_SKILLS = 6;
  private final StudentRepository studentRepository;
  private final StudentProgressRepository studentProgressRepository;
  private final SkillLevelProgressRepository skillLevelProgressRepository;
  private final TraceService traceService;
  private final TraceRepository traceRepository;
  private final AdditionalSkillRepository additionalSkillRepository;
  private final AdditionalSkillProgressRepository additionalSkillProgressRepository;

  @Override
  public boolean isStudentFollowingAPCProgram() {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    var studentProgress = studentProgressRepository.findAllAPCByStudent(student);
    return !studentProgress.isEmpty();
  }

  @Override
  public List<StudentProgress> getAllCurrentStudentProgress() {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    log.debug("{} fetched his student progresses", student);
    return studentProgressRepository.findAllByStudent(student).stream()
        .filter(StudentProgress::isCurrent)
        .toList();
  }

  @Override
  public Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>>
      getStudentProgressOverview() {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    var studentProgresses =
        studentProgressRepository.findAllByStudent(student).stream()
            .filter(StudentProgress::isCurrent)
            .toList();

    return studentProgresses.stream()
        .collect(
            Collectors.toMap(
                Function.identity(),
                studentProgress ->
                    studentProgress.getCurrentSkillLevels().stream()
                        .limit(MAX_SKILLS / studentProgresses.size())
                        .map(
                            skillLevelProgress ->
                                new SkillLevelProgressWithTraceCountData(
                                    skillLevelProgress,
                                    traceRepository.linkedWith(skillLevelProgress).size()))
                        .toList()));
  }

  @Override
  public Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> getStudentProgressView(
      SortCriteria sortCriteria) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    return studentProgressRepository.findAllByStudent(student).stream()
        .filter(StudentProgress::isCurrent)
        .sorted(StudentProgress.comparatorOf(sortCriteria))
        .collect(
            Collectors.toMap(
                Function.identity(),
                studentProgress ->
                    studentProgress.getCurrentSkillLevels().stream()
                        .sorted(SkillLevelProgress.comparatorOf(sortCriteria))
                        .map(
                            skillLevelProgress ->
                                new SkillLevelProgressWithTraceCountData(
                                    skillLevelProgress,
                                    traceRepository.linkedWith(skillLevelProgress).size()))
                        .toList(),
                (v1, v2) -> v1,
                LinkedHashMap::new));
  }

  @Override
  public PagedResult<SkillProgressData> getAllTimeSkillsView(
      SortCriteria sortCriteria, PageCriteria pageCriteria) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    var studentProgresses = studentProgressRepository.findAllByStudent(student).stream().toList();

    var skillProgresses =
        studentProgresses.stream()
            .filter(studentProgress -> studentProgress.getStartDate().isBefore(LocalDate.now()))
            .flatMap(
                studentProgress ->
                    studentProgress.getCurrentSkillLevels().stream()
                        .map(
                            currentSkillLevel ->
                                new SkillProgressData(
                                    currentSkillLevel.getSkillLevel().getSkill(),
                                    studentProgress,
                                    new SkillLevelProgressWithTraceCountData(
                                        currentSkillLevel,
                                        traceRepository.linkedWith(currentSkillLevel).size()))))
            .sorted(
                Comparator.comparing(
                        (SkillProgressData skillProgress) ->
                            skillProgress.studentProgress().isCurrent() ? 0 : 1)
                    .thenComparing(SkillProgressData.comparatorOf(sortCriteria)))
            .toList();

    return new PagedResult<>(
        skillProgresses.stream()
            .skip((long) (pageCriteria.page()) * pageCriteria.pageSize())
            .limit(pageCriteria.pageSize())
            .toList(),
        new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), skillProgresses.size()));
  }

  @Override
  public PagedResult<SkillLevelProgress> searchSkillLevel(
      String keyword, PageCriteria pageCriteria) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    log.debug("Searching SkillLevelProgress for {} with pagination {}", student, pageCriteria);

    return skillLevelProgressRepository.findAllByStudent(student, pageCriteria, keyword);
  }

  @Override
  public List<SkillLevelProgress> getSkillLevelsBySkillId(UUID skillId) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    return skillLevelProgressRepository.findAllByStudentAndSkillId(student, skillId);
  }

  @Override
  public List<Skill> getAllSkillList() {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    return studentProgressRepository.findAllByStudent(student).stream()
        .flatMap(
            studentProgress ->
                studentProgress.getAllSkillLevels().stream()
                    .map(skillLevelProgress -> skillLevelProgress.getSkillLevel().getSkill()))
        .distinct()
        .toList();
  }

  @Override
  public PagedResult<AdditionalSkillProgress> getAdditionalSkillsProgresses(
      PageCriteria pageCriteria) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    return additionalSkillProgressRepository.findAllByStudent(student, pageCriteria);
  }

  @Override
  public AdditionalSkillProgress createAdditionalSkillProgress(
      UUID additionalSkillId,
      EAdditionalSkillType type,
      EAdditionalSkillLevel level,
      String description) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    try {
      checkDescriptionField(description);
      Optional<AdditionalSkill> additionalSkill =
          additionalSkillRepository.findById(additionalSkillId);
      AdditionalSkillProgress additionalSkillProgress =
          AdditionalSkillProgress.create(
              student,
              additionalSkill.orElseThrow(AdditionalSkillNotFoundException::new),
              level,
              description);
      if (additionalSkillProgressRepository.additionalSkillProgressAlreadyExists(
          additionalSkillProgress)) {
        log.error(
            "Failed to add additional skill [{}] for student [{}] because it already exists",
            additionalSkillId,
            student);
        throw new DuplicateAdditionalSkillException();
      }
      return additionalSkillProgressRepository.save(additionalSkillProgress);
    } catch (AdditionalSkillNotFoundException e) {
      log.error("Failed to add additional skill for student [{}]: {}", student, e.getMessage());
      throw e;
    }
  }

  @Override
  public AdditionalSkillProgress updateAdditionalSkillProgress(
      UUID additionalSkillProgressId, EAdditionalSkillLevel level, String description) {
    checkDescriptionField(description);
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);

    AdditionalSkillProgress additionalSkillProgress =
        additionalSkillProgressRepository
            .findById(additionalSkillProgressId)
            .orElseThrow(AdditionalSkillProgressNotFoundException::new);

    if (!additionalSkillProgress.getStudent().getId().equals(student.getId())) {
      throw new UserNotAuthorizedException();
    }

    additionalSkillProgress.setLevel(level);
    additionalSkillProgress.setDescription(description);

    return additionalSkillProgressRepository.save(additionalSkillProgress);
  }

  @Override
  public PagedResult<AdditionalSkillProgress> searchAdditionalSkill(
      String keyword, PageCriteria pageCriteria) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    return additionalSkillProgressRepository.findAllByStudent(student, pageCriteria, keyword);
  }

  @Override
  public AdditionalSkillProgressDetails getAdditionalSkillProgressDetails(
      UUID additionalSkillProgressId) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);

    AdditionalSkillProgress additionalSkillProgress =
        additionalSkillProgressRepository
            .findById(additionalSkillProgressId)
            .orElseThrow(AdditionalSkillProgressNotFoundException::new);

    if (!additionalSkillProgress.getStudent().getId().equals(student.getId())) {
      throw new UserNotAuthorizedException();
    }

    List<Trace> traces =
        traceService.getTracesLinkedWithAdditionalSkillProgress(
            student.getUser(), additionalSkillProgress);
    return new AdditionalSkillProgressDetails(
        additionalSkillProgress,
        traces.stream()
            .map(
                trace ->
                    new TraceWithProjectNameData(trace, traceService.programNameOfTrace(trace)))
            .toList());
  }

  private static void checkDescriptionField(String description) {
    if (description != null && description.length() > DESCRIPTION_LENGTH_MAX) {
      log.error(
          "Description too long: {} characters (max = " + DESCRIPTION_LENGTH_MAX + ")",
          description.length());
      throw new InvalidDescriptionException(
          "Description exceeds 400 characters (actual: " + description.length() + ")");
    }
  }
}
