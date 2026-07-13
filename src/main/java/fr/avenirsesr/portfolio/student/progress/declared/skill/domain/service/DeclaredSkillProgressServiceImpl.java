package fr.avenirsesr.portfolio.student.progress.declared.skill.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_DESCRIPTION_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.requireNotNull;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateOptionalEnrichedTextMaxLength;

import fr.avenirsesr.portfolio.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDetailsDTO;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.declaredskill.domain.exception.DeclaredSkillNotFoundException;
import fr.avenirsesr.portfolio.declaredskill.domain.exception.DuplicateDeclaredSkillException;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.declaredskill.domain.port.input.DeclaredSkillSyncService;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.client.ExternalSkillClient;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationsData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.output.repository.DeclaredSkillProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.data.TraceAssociationData;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class DeclaredSkillProgressServiceImpl implements DeclaredSkillProgressService {
  private final TraceService traceService;
  private final DeclaredSkillSyncService declaredSkillSyncService;
  private final DeclaredSkillProgressRepository declaredSkillProgressRepository;
  private final ExternalSkillClient externalSkillClient;
  private final LoggedInUserService loggedInUserService;
  private final DeclaredActivityService declaredActivityService;
  private final AssociationService associationService;
  private final AssociationSearchHelper associationSearchHelper;

  @Override
  public PagedResult<DeclaredSkillProgress> getDeclaredSkillsProgresses(PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    return declaredSkillProgressRepository.findAllByStudent(
        student, pageCriteria, new SortCriteria(ESortField.NAME, ESortOrder.ASC));
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
      UUID declaredSkillProgressId, EDeclaredSkillLevel level, String reflection) {
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
  public DeclaredSkillAssociationsData associateDeclaredSkillWithActivities(
      UUID declaredSkillId, List<UUID> declaredActivityIds) {
    fetchAndCheckLoggedInStudentAuthorization(declaredSkillId);
    Student student = loggedInUserService.getLoggedInStudent();
    var activities = declaredActivityService.findAllDeclaredActivitiesByIds(declaredActivityIds);

    if (!new HashSet<>(activities.stream().map(DeclaredActivity::getId).toList())
        .containsAll(declaredActivityIds)) {
      throw new DeclaredActivityNotFoundException();
    }

    if (!activities.stream().allMatch(activity -> activity.getStudent().equals(student))) {
      throw new UserNotAuthorizedException();
    }

    associationService.createAll(
        declaredActivityIds.stream()
            .map(
                activityId ->
                    new AssociationData(
                        activityId,
                        declaredSkillId,
                        EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL))
            .toList());

    return getAssociationsOf(declaredSkillId);
  }

  @Override
  public void deleteAssociations(UUID declaredSkillProgressId, List<UUID> idsToDelete) {
    var declaredSkillProgress = fetchAndCheckLoggedInStudentAuthorization(declaredSkillProgressId);

    var associationIds =
        associationService
            .getAllOf(
                declaredSkillProgress.getId(),
                DeclaredSkillProgress.class,
                List.of(
                    EAssociationType.TRACE_DECLARED_SKILL,
                    EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL))
            .stream()
            .map(Association::getId)
            .toList();

    if (!new HashSet<>(associationIds).containsAll(idsToDelete)) {
      throw new UserNotAuthorizedException();
    }

    associationService.deleteAllByIds(idsToDelete);
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

    var associations =
        associationService.getAllOf(
            skill.getId(),
            DeclaredSkillProgress.class,
            List.of(
                EAssociationType.TRACE_DECLARED_SKILL,
                EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL));

    var traceAssociationIds =
        associations.stream()
            .filter(a -> a.getAssociationType() == EAssociationType.TRACE_DECLARED_SKILL)
            .map(Association::getId1)
            .toList();

    var activityAssociationIds =
        associations.stream()
            .filter(
                a -> a.getAssociationType() == EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL)
            .map(Association::getId1)
            .toList();

    var traces = traceService.findAllTracesById(traceAssociationIds);
    var declaredActivities =
        declaredActivityService.findAllDeclaredActivitiesByIds(activityAssociationIds);

    var activityStatuses = declaredActivityService.getDeclaredActivityStatus(declaredActivities);

    return new DeclaredSkillAssociationsData(
        associations.stream()
            .filter(a -> a.getAssociationType() == EAssociationType.TRACE_DECLARED_SKILL)
            .map(
                a ->
                    new TraceAssociationData(
                        a.getId(),
                        traces.stream()
                            .filter(t -> t.getId().equals(a.getId1()))
                            .findAny()
                            .orElseThrow(TraceNotFoundException::new)))
            .toList(),
        associations.stream()
            .filter(
                a -> a.getAssociationType() == EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL)
            .map(
                a -> {
                  DeclaredActivity activity =
                      declaredActivities.stream()
                          .filter(declaredActivity -> declaredActivity.getId().equals(a.getId1()))
                          .findAny()
                          .orElseThrow(DeclaredActivityNotFoundException::new);

                  return new DeclaredActivityAssociationData(
                      a.getId(), activity, activityStatuses.get(activity));
                })
            .toList());
  }

  @Override
  public PagedResult<DeclaredSkillProgress> searchDeclaredSkill(
      String keyword, PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    return declaredSkillProgressRepository.findAllByStudent(
        student, pageCriteria, keyword, new SortCriteria(ESortField.NAME, ESortOrder.ASC));
  }

  @Override
  public PagedResult<AssociationSearchResultData> searchDeclaredSkillsForAssociation(
      UUID excludeAssociatedWithElementId,
      EAssociationContextType contextType,
      String keyword,
      PageCriteria pageCriteria) {
    var skills = searchDeclaredSkill(keyword, pageCriteria);

    if (contextType == null) {
      return associationSearchHelper.searchForAssociation(
          null,
          null,
          null,
          null,
          skills,
          AvenirsBaseModel::getId,
          ds -> ds.getSkill().getLibelle(),
          ds -> ds.getSkill().getType().name(),
          ds -> false);
    }

    EAssociationType associationType = getAssociationType(contextType);

    return associationSearchHelper.searchForAssociation(
        excludeAssociatedWithElementId,
        contextType.toClass(),
        associationType,
        associationType.idExtractorFor(DeclaredSkillProgress.class),
        skills,
        AvenirsBaseModel::getId,
        ds -> ds.getSkill().getLibelle(),
        ds -> ds.getSkill().getType().name(),
        ds -> false);
  }

  @Override
  public List<DeclaredSkillProgress> findAllDeclaredSkillProgressesByIds(List<UUID> ids) {
    return declaredSkillProgressRepository.findAllById(ids);
  }

  private EAssociationType getAssociationType(EAssociationContextType contextType) {
    return switch (contextType) {
      case TRACE -> EAssociationType.TRACE_DECLARED_SKILL;
      case DECLARED_ACTIVITY -> EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL;
      case DECLARED_SKILL, DECLARED_EXPERIENCE -> throw new UnsupportedOperationException();
    };
  }
}
