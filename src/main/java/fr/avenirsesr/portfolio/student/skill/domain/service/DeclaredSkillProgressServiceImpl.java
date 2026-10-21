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
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationCount;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillProgressData;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DuplicateDeclaredSkillException;
import fr.avenirsesr.portfolio.student.skill.domain.exception.SkillNotSelfDeclarableException;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.skill.domain.port.output.repository.DeclaredSkillProgressRepository;
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
public class DeclaredSkillProgressServiceImpl implements DeclaredSkillProgressService {
  private final SkillService skillService;
  private final DeclaredSkillProgressRepository declaredSkillProgressRepository;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;

  @Override
  public PagedResult<DeclaredSkillProgressData> getDeclaredSkillsProgresses(
      PageCriteria pageCriteria, Boolean isValorized) {
    Student student = loggedInUserService.getLoggedInStudent();
    var pagedDeclaredSkillProgresses =
        declaredSkillProgressRepository.findAllByStudent(
            student, pageCriteria, isValorized, new SortCriteria(ESortField.NAME, ESortOrder.ASC));

    return new PagedResult<>(
        getDeclaredSkillProgressData(pagedDeclaredSkillProgresses.content()),
        pagedDeclaredSkillProgresses.pageInfo());
  }

  @Override
  public DeclaredSkillProgressData createDeclaredSkillProgress(
      UUID skillId, EDeclaredSkillLevel level, String reflection) {
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

    DeclaredSkillProgress declaredSkillProgress =
        DeclaredSkillProgress.create(student, skill, level, reflection);

    if (declaredSkillProgressRepository.declaredSkillProgressAlreadyExists(declaredSkillProgress)) {
      log.error(
          "Failed to add skill [{}] for student [{}] because it already exists", skillId, student);
      throw new DuplicateDeclaredSkillException();
    }

    return toData(declaredSkillProgressRepository.save(declaredSkillProgress), skill);
  }

  @Override
  public DeclaredSkillProgressData updateDeclaredSkillProgress(
      UUID declaredSkillProgressId,
      EDeclaredSkillLevel level,
      String reflection,
      boolean valorized) {
    Student student = loggedInUserService.getLoggedInStudent();
    validateOptionalEnrichedTextMaxLength("reflection", reflection, RICH_DESCRIPTION_LENGTH);

    DeclaredSkillProgress declaredSkillProgress =
        declaredSkillProgressRepository
            .findById(declaredSkillProgressId)
            .orElseThrow(DeclaredSkillProgressNotFoundException::new);

    if (!declaredSkillProgress.getStudent().getId().equals(student.getId())) {
      throw new UserNotAuthorizedException();
    }

    declaredSkillProgress.setLevel(level);
    declaredSkillProgress.setReflection(reflection);
    declaredSkillProgress.setValorized(valorized);

    DeclaredSkillProgress saved = declaredSkillProgressRepository.save(declaredSkillProgress);
    return toData(
        saved, skillService.getWithAncestorsById(declaredSkillProgress.getSkill().getId()));
  }

  @Override
  public DeclaredSkillProgressDetails getDeclaredSkillProgressDetails(
      UUID declaredSkillProgressId) {
    Student student = loggedInUserService.getLoggedInStudent();

    DeclaredSkillProgress declaredSkillProgress =
        declaredSkillProgressRepository
            .findById(declaredSkillProgressId, FetchGraph.init().fetch("student").fetch("skill"))
            .orElseThrow(DeclaredSkillProgressNotFoundException::new);

    if (!declaredSkillProgress.getStudent().getId().equals(student.getId())) {
      throw new UserNotAuthorizedException();
    }

    return getDeclaredSkillProgressDetails(List.of(declaredSkillProgress)).stream()
        .findFirst()
        .orElseThrow(DeclaredSkillProgressNotFoundException::new);
  }

  @Override
  public List<DeclaredSkillProgressDetails> getDeclaredSkillProgressDetails(
      List<DeclaredSkillProgress> declaredSkillProgresses) {
    if (declaredSkillProgresses.isEmpty()) {
      return List.of();
    }

    var skillById = getSkillsWithAncestors(declaredSkillProgresses);
    var pathBySkillId = skillService.getDetailedPaths(List.copyOf(skillById.values()));

    return declaredSkillProgresses.stream()
        .map(
            declaredSkillProgress -> {
              UUID skillId = declaredSkillProgress.getSkill().getId();
              return new DeclaredSkillProgressDetails(
                  declaredSkillProgress,
                  skillById.get(skillId),
                  pathBySkillId.getOrDefault(skillId, List.of()));
            })
        .toList();
  }

  @Override
  public List<DeclaredSkillProgressData> getDeclaredSkillProgressData(
      List<DeclaredSkillProgress> declaredSkillProgresses) {
    if (declaredSkillProgresses.isEmpty()) {
      return List.of();
    }

    var skillById = getSkillsWithAncestors(declaredSkillProgresses);
    var associationsCountByDeclaredSkillProgress = getAssociationCounts(declaredSkillProgresses);

    return declaredSkillProgresses.stream()
        .map(
            declaredSkillProgress ->
                new DeclaredSkillProgressData(
                    declaredSkillProgress,
                    skillById.get(declaredSkillProgress.getSkill().getId()),
                    associationsCountByDeclaredSkillProgress.get(declaredSkillProgress)))
        .toList();
  }

  @Override
  public void deleteDeclaredSkillProgresses(List<UUID> declaredSkillProgressIds) {
    Student student = loggedInUserService.getLoggedInStudent();

    List<DeclaredSkillProgress> declaredSkillProgressList =
        declaredSkillProgressRepository.findAllById(declaredSkillProgressIds);

    if (!new HashSet<>(
            declaredSkillProgressList.stream().map(DeclaredSkillProgress::getId).toList())
        .containsAll(declaredSkillProgressIds)) {
      throw new DeclaredSkillProgressNotFoundException();
    }

    if (declaredSkillProgressList.stream()
        .anyMatch(declaredSkillProgress -> !declaredSkillProgress.getStudent().equals(student))) {
      throw new UserNotAuthorizedException();
    }

    associationService.deleteAllOf(declaredSkillProgressIds, DeclaredSkillProgress.class);

    declaredSkillProgressRepository.removeAllFromDatabase(declaredSkillProgressList);
    log.info("DeclaredSkillProgressIds {} successfully deleted", declaredSkillProgressIds);
  }

  @Override
  public PagedResult<DeclaredSkillProgress> searchDeclaredSkill(
      String keyword, PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    return declaredSkillProgressRepository.findAllByStudent(
        student, pageCriteria, keyword, new SortCriteria(ESortField.NAME, ESortOrder.ASC));
  }

  @Override
  public List<DeclaredSkillProgress> findAllDeclaredSkillProgressesByIds(List<UUID> ids) {
    return declaredSkillProgressRepository.findAllById(ids);
  }

  @Override
  public List<UUID> getDeclaredSkillIds() {
    Student student = loggedInUserService.getLoggedInStudent();
    return declaredSkillProgressRepository.findAllByStudent(student).stream()
        .map(declaredSkillProgress -> declaredSkillProgress.getSkill().getId())
        .distinct()
        .toList();
  }

  private DeclaredSkillProgressData toData(
      DeclaredSkillProgress declaredSkillProgress, Skill skill) {
    return new DeclaredSkillProgressData(
        declaredSkillProgress,
        skill,
        getAssociationCounts(List.of(declaredSkillProgress)).get(declaredSkillProgress));
  }

  private Map<UUID, Skill> getSkillsWithAncestors(
      List<DeclaredSkillProgress> declaredSkillProgresses) {
    List<UUID> skillIds =
        declaredSkillProgresses.stream()
            .map(declaredSkillProgress -> declaredSkillProgress.getSkill().getId())
            .distinct()
            .toList();

    return skillService.getAllWithAncestorsById(skillIds).stream()
        .collect(Collectors.toMap(Skill::getId, Function.identity()));
  }

  private Map<DeclaredSkillProgress, DeclaredSkillAssociationCount> getAssociationCounts(
      List<DeclaredSkillProgress> declaredSkillProgresses) {
    var ids = declaredSkillProgresses.stream().map(DeclaredSkillProgress::getId).toList();

    var traceAssociationsCountById =
        associationService.countAllOf(
            ids, DeclaredSkillProgress.class, EAssociationType.TRACE_DECLARED_SKILL);
    var declaredActivityAssociationsCountById =
        associationService.countAllOf(
            ids, DeclaredSkillProgress.class, EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL);

    return declaredSkillProgresses.stream()
        .collect(
            Collectors.toMap(
                Function.identity(),
                declaredSkillProgress ->
                    new DeclaredSkillAssociationCount(
                        traceAssociationsCountById
                            .getOrDefault(declaredSkillProgress.getId(), 0L)
                            .intValue(),
                        declaredActivityAssociationsCountById
                            .getOrDefault(declaredSkillProgress.getId(), 0L)
                            .intValue())));
  }
}
