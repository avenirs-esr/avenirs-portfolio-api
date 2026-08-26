package fr.avenirsesr.portfolio.student.skill.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_DESCRIPTION_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.requireNotNull;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateOptionalEnrichedTextMaxLength;

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
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceAssociationData;
import fr.avenirsesr.portfolio.student.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
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
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceAssociationData;
import fr.avenirsesr.portfolio.student.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
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
  private final DeclaredExperienceService declaredExperienceService;

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
  public DeclaredSkillAssociationsData associateDeclaredSkillWithDeclaredExperiences(
      UUID declaredSkillId, List<UUID> declaredExperienceIds) {
    fetchAndCheckLoggedInStudentAuthorization(declaredSkillId);
    Student student = loggedInUserService.getLoggedInStudent();
    var uniqueDeclaredExperienceIds = declaredExperienceIds.stream().distinct().toList();
    var experiences = declaredExperienceService.findAllByIds(uniqueDeclaredExperienceIds);

    if (!new HashSet<>(experiences.stream().map(DeclaredExperience::getId).toList())
        .containsAll(uniqueDeclaredExperienceIds)) {
      throw new DeclaredExperienceNotFoundException();
    }

    if (!experiences.stream().allMatch(experience -> experience.getStudent().equals(student))) {
      throw new UserNotAuthorizedException();
    }

    associationService.createAll(
        uniqueDeclaredExperienceIds.stream()
            .map(
                experienceId ->
                    new AssociationData(
                        experienceId,
                        declaredSkillId,
                        EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL))
            .toList());

    return getAssociationsOf(declaredSkillId);
  }

  @Override
  public DeclaredSkillAssociationsData associateDeclaredSkillWithTraces(
      UUID declaredSkillId, List<UUID> traceIds) {
    fetchAndCheckLoggedInStudentAuthorization(declaredSkillId);
    Student student = loggedInUserService.getLoggedInStudent();
    var uniqueTraceIds = traceIds.stream().distinct().toList();
    var traces = traceService.findAllTracesById(uniqueTraceIds);

    if (!new HashSet<>(traces.stream().map(Trace::getId).toList()).containsAll(uniqueTraceIds)) {
      throw new TraceNotFoundException();
    }

    if (!traces.stream().allMatch(trace -> trace.getStudent().equals(student))) {
      throw new UserNotAuthorizedException();
    }

    associationService.createAll(
        uniqueTraceIds.stream()
            .map(
                traceId ->
                    new AssociationData(
                        traceId, declaredSkillId, EAssociationType.TRACE_DECLARED_SKILL))
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
                EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL));

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

    var experienceAssociationIds =
        associations.stream()
            .filter(
                a -> a.getAssociationType() == EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL)
            .map(Association::getId1)
            .toList();

    var traces = traceService.findAllTracesById(traceAssociationIds);
    var declaredActivities =
        declaredActivityService.findAllDeclaredActivitiesByIds(activityAssociationIds);
    var declaredExperiences = declaredExperienceService.findAllByIds(experienceAssociationIds);

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
            .toList(),
        associations.stream()
            .filter(
                a -> a.getAssociationType() == EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL)
            .map(
                a ->
                    new DeclaredExperienceAssociationData(
                        a.getId(),
                        declaredExperiences.stream()
                            .filter(experience -> experience.getId().equals(a.getId1()))
                            .findAny()
                            .orElseThrow(DeclaredExperienceNotFoundException::new)))
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
      case DECLARED_EXPERIENCE -> EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL;
      case DECLARED_SKILL -> throw new UnsupportedOperationException();
    };
  }
}
