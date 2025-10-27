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
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillLevelProgressWithTraceCountData;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.progress.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class StudentProgressServiceImpl implements StudentProgressService {
  private static final int MAX_SKILLS = 6;
  private final StudentProgressRepository studentProgressRepository;
  private final SkillLevelProgressRepository skillLevelProgressRepository;
  private final TraceRepository traceRepository;
  private final AdditionalSkillRepository additionalSkillRepository;
  private final AdditionalSkillProgressRepository additionalSkillProgressRepository;

  @Override
  public boolean isStudentFollowingAPCProgram(Student student) {
    var studentProgress = studentProgressRepository.findAllAPCByStudent(student);
    return !studentProgress.isEmpty();
  }

  @Override
  public Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>>
      getStudentProgressOverview(Student student) {
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
      Student student, SortCriteria sortCriteria) {
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
      Student student, SortCriteria sortCriteria, PageCriteria pageCriteria) {

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
      Student student, String keyword, PageCriteria pageCriteria) {
    log.debug("Searching SkillLevelProgress for {} with pagination {}", student, pageCriteria);

    return skillLevelProgressRepository.findAllByStudent(student, pageCriteria, keyword);
  }

  @Override
  public List<SkillLevelProgress> getSkillLevelsBySkillId(Student student, UUID skillId) {
    return skillLevelProgressRepository.findAllByStudentAndSkillId(student, skillId);
  }

  @Override
  public List<Skill> getAllSkillList(Student student) {
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
      Student student, PageCriteria pageCriteria) {
    return additionalSkillProgressRepository.findAllByStudent(student, pageCriteria);
  }

  @Override
  public AdditionalSkillProgress createAdditionalSkillProgress(
      Student student,
      UUID additionalSkillId,
      EAdditionalSkillType type,
      EAdditionalSkillLevel level,
      String description) {
    try {
      if (description != null && description.length() > 400) {
        log.error("Description too long: {} characters (max = 400)", description.length());
        throw new InvalidDescriptionException(
            "Description exceeds 400 characters (actual: " + description.length() + ")");
      }

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
  public PagedResult<AdditionalSkillProgress> searchAdditionalSkill(
      Student student, String keyword, PageCriteria pageCriteria) {
    return additionalSkillProgressRepository.findAllByStudent(student, pageCriteria, keyword);
  }
}
