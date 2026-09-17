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
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDetailsDTO;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationCount;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationsData;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillProgressData;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DeclaredSkillNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DuplicateDeclaredSkillException;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillSyncService;
import fr.avenirsesr.portfolio.student.skill.domain.port.output.repository.DeclaredSkillProgressRepository;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.client.ExternalSkillClient;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class DeclaredSkillProgressServiceImpl implements DeclaredSkillProgressService {
  private final DeclaredSkillSyncService declaredSkillSyncService;
  private final DeclaredSkillProgressRepository declaredSkillProgressRepository;
  private final ExternalSkillClient externalSkillClient;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;

  @Override
  public PagedResult<DeclaredSkillProgressData> getDeclaredSkillsProgresses(
      PageCriteria pageCriteria, Boolean isValorized) {
    Student student = loggedInUserService.getLoggedInStudent();
    var pagedDeclaredSkillProgresses =
        declaredSkillProgressRepository.findAllByStudent(
            student, pageCriteria, isValorized, new SortCriteria(ESortField.NAME, ESortOrder.ASC));

    var associationsCountByDeclaredSkillProgress =
        getAssociationCounts(pagedDeclaredSkillProgresses.content());

    return new PagedResult<>(
        pagedDeclaredSkillProgresses.content().stream()
            .map(
                declaredSkillProgress ->
                    new DeclaredSkillProgressData(
                        declaredSkillProgress,
                        associationsCountByDeclaredSkillProgress.get(declaredSkillProgress)))
            .toList(),
        pagedDeclaredSkillProgresses.pageInfo());
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

  @Override
  public DeclaredSkillProgress createDeclaredSkillProgress(
      UUID declaredSkillId, EExternalSkillType type, EDeclaredSkillLevel level, String reflection) {
    Student student = loggedInUserService.getLoggedInStudent();
    requireNotNull("id", declaredSkillId);
    requireNotNull("type", type);
    requireNotNull("level", level);
    try {
      validateOptionalEnrichedTextMaxLength("reflection", reflection, RICH_DESCRIPTION_LENGTH);
      DeclaredSkill declaredSkill =
          declaredSkillSyncService
              .getOrCreateFromExternalSkill(declaredSkillId)
              .orElseThrow(DeclaredSkillNotFoundException::new);
      DeclaredSkillProgress declaredSkillProgress =
          DeclaredSkillProgress.create(student, declaredSkill, level, reflection);
      if (declaredSkillProgressRepository.declaredSkillProgressAlreadyExists(
          declaredSkillProgress)) {
        log.error(
            "Failed to add declared skill [{}] for student [{}] because it already exists",
            declaredSkillId,
            student);
        throw new DuplicateDeclaredSkillException();
      }
      return declaredSkillProgressRepository.save(declaredSkillProgress);
    } catch (DeclaredSkillNotFoundException e) {
      log.error("Failed to add declared skill for student [{}]: {}", student, e.getMessage());
      throw e;
    }
  }

  @Override
  public DeclaredSkillProgress updateDeclaredSkillProgress(
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

    return declaredSkillProgressRepository.save(declaredSkillProgress);
  }

  @Override
  public DeclaredSkillProgressDetails getDeclaredSkillProgressDetails(
      UUID declaredSkillProgressId) {
    Student student = loggedInUserService.getLoggedInStudent();

    DeclaredSkillProgress declaredSkillProgress =
        declaredSkillProgressRepository
            .findById(
                declaredSkillProgressId, FetchGraph.init().fetch("student").fetch("declaredSkill"))
            .orElseThrow(DeclaredSkillProgressNotFoundException::new);

    if (!declaredSkillProgress.getStudent().getId().equals(student.getId())) {
      throw new UserNotAuthorizedException();
    }

    return toDeclaredSkillProgressDetails(declaredSkillProgress);
  }

  @Override
  public List<DeclaredSkillProgressDetails> getDeclaredSkillProgressDetails(
      List<DeclaredSkillProgress> declaredSkillProgresses) {
    return declaredSkillProgresses.stream().map(this::toDeclaredSkillProgressDetails).toList();
  }

  private DeclaredSkillProgressDetails toDeclaredSkillProgressDetails(
      DeclaredSkillProgress declaredSkillProgress) {
    UUID id = declaredSkillProgress.getSkill().getId();
    ExternalSkillDetailsDTO externalSkillDetails =
        externalSkillClient
            .getExternalSkillDetails(id)
            .orElse(new ExternalSkillDetailsDTO(id, "", List.of(), null));

    return new DeclaredSkillProgressDetails(
        declaredSkillProgress, externalSkillDetails.categoryPath());
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
  public DeclaredSkillAssociationsData associate(
      UUID declaredSkillId, List<UUID> associatedIds, EAssociationType associationType) {
    var declaredSkillProgress = fetchAndCheckLoggedInStudentAuthorization(declaredSkillId);

    associationService.associate(
        declaredSkillProgress.getId(), DeclaredSkillProgress.class, associatedIds, associationType);

    return getAssociationsOf(declaredSkillId);
  }

  @Override
  public void deleteAssociations(UUID declaredSkillProgressId, List<UUID> idsToDelete) {
    var declaredSkillProgress = fetchAndCheckLoggedInStudentAuthorization(declaredSkillProgressId);

    associationService.unassociate(
        declaredSkillProgress.getId(), DeclaredSkillProgress.class, idsToDelete);
  }

  private DeclaredSkillProgress fetchAndCheckLoggedInStudentAuthorization(UUID declaredSkillId) {
    Student student = loggedInUserService.getLoggedInStudent();
    var skill =
        declaredSkillProgressRepository
            .findById(declaredSkillId)
            .orElseThrow(DeclaredSkillProgressNotFoundException::new);

    if (!skill.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }

    return skill;
  }

  @Override
  public DeclaredSkillAssociationsData getAssociationsOf(UUID declaredSkillId) {
    var skill = fetchAndCheckLoggedInStudentAuthorization(declaredSkillId);

    var associatedElements =
        associationService.getAllAssociatedElementsOf(skill.getId(), DeclaredSkillProgress.class);

    return new DeclaredSkillAssociationsData(
        associatedElements.traceAssociations(),
        associatedElements.declaredActivityAssociations(),
        associatedElements.declaredExperienceAssociations());
  }

  @Override
  public PagedResult<DeclaredSkillProgress> searchDeclaredSkill(
      String keyword, PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    return declaredSkillProgressRepository.findAllByStudent(
        student, pageCriteria, keyword, new SortCriteria(ESortField.NAME, ESortOrder.ASC));
  }

  @Override
  public PagedResult<AssociationSearchResultData> searchForAssociation(
      UUID declaredSkillId,
      EAssociationContextType contextType,
      String keyword,
      PageCriteria pageCriteria) {
    fetchAndCheckLoggedInStudentAuthorization(declaredSkillId);

    return associationService.searchForAssociation(
        declaredSkillId, DeclaredSkillProgress.class, contextType, keyword, pageCriteria);
  }

  @Override
  public List<DeclaredSkillProgress> findAllDeclaredSkillProgressesByIds(List<UUID> ids) {
    return declaredSkillProgressRepository.findAllById(ids);
  }

  @Override
  public List<UUID> getAssociatedExternalSkillIds() {
    Student student = loggedInUserService.getLoggedInStudent();
    return declaredSkillProgressRepository.findAllByStudent(student).stream()
        .map(declaredSkillProgress -> declaredSkillProgress.getSkill().getId())
        .distinct()
        .toList();
  }
}
