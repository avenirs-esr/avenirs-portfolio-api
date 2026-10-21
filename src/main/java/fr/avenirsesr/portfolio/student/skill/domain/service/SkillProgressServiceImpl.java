package fr.avenirsesr.portfolio.student.skill.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_DESCRIPTION_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.requireNotNull;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateOptionalEnrichedTextMaxLength;

import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.skill.domain.exception.SkillNotAssessableException;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.domain.port.input.SkillService;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressAssociationCount;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressDetails;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DuplicateSkillProgressException;
import fr.avenirsesr.portfolio.student.skill.domain.exception.SkillNotSelfDeclarableException;
import fr.avenirsesr.portfolio.student.skill.domain.exception.SkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.ESkillLevel;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.SkillProgressService;
import fr.avenirsesr.portfolio.student.skill.domain.port.output.repository.SkillProgressRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SkillProgressServiceImpl implements SkillProgressService {
  private final SkillService skillService;
  private final SkillProgressRepository skillProgressRepository;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;

  @Override
  public PagedResult<SkillProgressData> getSkillProgresses(
      PageCriteria pageCriteria, Boolean isValorized) {
    Student student = loggedInUserService.getLoggedInStudent();
    var pagedSkillProgresses =
        skillProgressRepository.findAllByStudent(
            student, pageCriteria, isValorized, new SortCriteria(ESortField.NAME, ESortOrder.ASC));

    return new PagedResult<>(
        getSkillProgressData(pagedSkillProgresses.content()), pagedSkillProgresses.pageInfo());
  }

  @Override
  public SkillProgressData createSkillProgress(UUID skillId, ESkillLevel level, String reflection) {
    Student student = loggedInUserService.getLoggedInStudent();
    requireNotNull("id", skillId);
    requireNotNull("level", level);
    validateOptionalEnrichedTextMaxLength("reflection", reflection, RICH_DESCRIPTION_LENGTH);

    Skill skill = skillService.getWithAncestorsById(skillId);

    if (!skill.isAssessable()) {
      log.error(
          "Failed to add skill [{}] for student [{}] because it is not assessable",
          skillId,
          student);
      throw new SkillNotAssessableException();
    }

    if (!skill.getReferential().isNational()) {
      log.error(
          "Failed to add skill [{}] for student [{}] because it does not belong to a national"
              + " referential",
          skillId,
          student);
      throw new SkillNotSelfDeclarableException();
    }

    SkillProgress skillProgress = SkillProgress.create(student, skill, level, reflection);

    if (skillProgressRepository.skillProgressAlreadyExists(skillProgress)) {
      log.error(
          "Failed to add skill [{}] for student [{}] because it already exists", skillId, student);
      throw new DuplicateSkillProgressException();
    }

    return toData(skillProgressRepository.save(skillProgress), skill);
  }

  @Override
  public SkillProgressData updateSkillProgress(
      UUID skillProgressId, ESkillLevel level, String reflection, boolean valorized) {
    Student student = loggedInUserService.getLoggedInStudent();
    validateOptionalEnrichedTextMaxLength("reflection", reflection, RICH_DESCRIPTION_LENGTH);

    SkillProgress skillProgress =
        skillProgressRepository
            .findById(skillProgressId)
            .orElseThrow(SkillProgressNotFoundException::new);

    if (!skillProgress.getStudent().getId().equals(student.getId())) {
      throw new UserNotAuthorizedException();
    }

    skillProgress.setLevel(level);
    skillProgress.setReflection(reflection);
    skillProgress.setValorized(valorized);

    SkillProgress saved = skillProgressRepository.save(skillProgress);
    return toData(saved, skillService.getWithAncestorsById(skillProgress.getSkill().getId()));
  }

  @Override
  public SkillProgressDetails getSkillProgressDetails(UUID skillProgressId) {
    Student student = loggedInUserService.getLoggedInStudent();

    SkillProgress skillProgress =
        skillProgressRepository
            .findById(skillProgressId, FetchGraph.init().fetch("student").fetch("skill"))
            .orElseThrow(SkillProgressNotFoundException::new);

    if (!skillProgress.getStudent().getId().equals(student.getId())) {
      throw new UserNotAuthorizedException();
    }

    return getSkillProgressDetails(List.of(skillProgress)).stream()
        .findFirst()
        .orElseThrow(SkillProgressNotFoundException::new);
  }

  @Override
  public List<SkillProgressDetails> getSkillProgressDetails(List<SkillProgress> skillProgresses) {
    if (skillProgresses.isEmpty()) {
      return List.of();
    }

    var skillById = getSkillsWithAncestors(skillProgresses);
    var pathBySkillId = skillService.getDetailedPaths(List.copyOf(skillById.values()));

    return skillProgresses.stream()
        .map(
            skillProgress -> {
              UUID skillId = skillProgress.getSkill().getId();
              return new SkillProgressDetails(
                  skillProgress,
                  skillById.get(skillId),
                  pathBySkillId.getOrDefault(skillId, List.of()));
            })
        .toList();
  }

  @Override
  public List<SkillProgressData> getSkillProgressData(List<SkillProgress> skillProgresses) {
    if (skillProgresses.isEmpty()) {
      return List.of();
    }

    var skillById = getSkillsWithAncestors(skillProgresses);
    var associationsCountBySkillProgress = getAssociationCounts(skillProgresses);

    return skillProgresses.stream()
        .map(
            skillProgress ->
                new SkillProgressData(
                    skillProgress,
                    skillById.get(skillProgress.getSkill().getId()),
                    associationsCountBySkillProgress.get(skillProgress)))
        .toList();
  }

  @Override
  public void deleteSkillProgresses(List<UUID> skillProgressIds) {
    Student student = loggedInUserService.getLoggedInStudent();

    List<SkillProgress> skillProgressList = skillProgressRepository.findAllById(skillProgressIds);

    if (!new HashSet<>(skillProgressList.stream().map(SkillProgress::getId).toList())
        .containsAll(skillProgressIds)) {
      throw new SkillProgressNotFoundException();
    }

    if (skillProgressList.stream()
        .anyMatch(skillProgress -> !skillProgress.getStudent().equals(student))) {
      throw new UserNotAuthorizedException();
    }

    associationService.deleteAllOf(skillProgressIds, SkillProgress.class);

    skillProgressRepository.removeAllFromDatabase(skillProgressList);
    log.info("Skill progresses {} successfully deleted", skillProgressIds);
  }

  @Override
  public PagedResult<SkillProgress> searchSkillProgress(String keyword, PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    return skillProgressRepository.findAllByStudent(
        student, pageCriteria, keyword, new SortCriteria(ESortField.NAME, ESortOrder.ASC));
  }

  @Override
  public List<SkillProgress> findAllSkillProgressesByIds(List<UUID> ids) {
    return skillProgressRepository.findAllById(ids);
  }

  @Override
  public List<UUID> getSkillIds() {
    Student student = loggedInUserService.getLoggedInStudent();
    return skillProgressRepository.findAllByStudent(student).stream()
        .map(skillProgress -> skillProgress.getSkill().getId())
        .distinct()
        .toList();
  }

  private SkillProgressData toData(SkillProgress skillProgress, Skill skill) {
    return new SkillProgressData(
        skillProgress, skill, getAssociationCounts(List.of(skillProgress)).get(skillProgress));
  }

  private Map<UUID, Skill> getSkillsWithAncestors(List<SkillProgress> skillProgresses) {
    List<UUID> skillIds =
        skillProgresses.stream()
            .map(skillProgress -> skillProgress.getSkill().getId())
            .distinct()
            .toList();

    return skillService.getAllWithAncestorsById(skillIds).stream()
        .collect(Collectors.toMap(Skill::getId, Function.identity()));
  }

  private Map<SkillProgress, SkillProgressAssociationCount> getAssociationCounts(
      List<SkillProgress> skillProgresses) {
    var ids = skillProgresses.stream().map(SkillProgress::getId).toList();

    var traceAssociationsCountById =
        associationService.countAllOf(
            ids, SkillProgress.class, EAssociationType.TRACE_DECLARED_SKILL);
    var declaredActivityAssociationsCountById =
        associationService.countAllOf(
            ids, SkillProgress.class, EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL);

    return skillProgresses.stream()
        .collect(
            Collectors.toMap(
                Function.identity(),
                skillProgress ->
                    new SkillProgressAssociationCount(
                        traceAssociationsCountById
                            .getOrDefault(skillProgress.getId(), 0L)
                            .intValue(),
                        declaredActivityAssociationsCountById
                            .getOrDefault(skillProgress.getId(), 0L)
                            .intValue())));
  }
}
