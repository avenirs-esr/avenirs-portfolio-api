package fr.avenirsesr.portfolio.student.progress.declared.skill.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.requireNotNull;

import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDetailsDTO;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.declaredskill.domain.exception.DeclaredSkillNotFoundException;
import fr.avenirsesr.portfolio.declaredskill.domain.exception.DuplicateDeclaredSkillException;
import fr.avenirsesr.portfolio.declaredskill.domain.exception.InvalidDescriptionException;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.declaredskill.domain.port.input.DeclaredSkillSyncService;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.client.ExternalSkillClient;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.output.repository.DeclaredSkillProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.data.TraceWithProjectNameData;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class DeclaredSkillProgressServiceImpl implements DeclaredSkillProgressService {
  public static final int DESCRIPTION_LENGTH_MAX = 400;
  private final TraceService traceService;
  private final DeclaredSkillSyncService declaredSkillSyncService;
  private final DeclaredSkillProgressRepository declaredSkillProgressRepository;
  private final ExternalSkillClient externalSkillClient;
  private final LoggedInUserService loggedInUserService;

  @Override
  public PagedResult<DeclaredSkillProgress> getDeclaredSkillsProgresses(PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    return declaredSkillProgressRepository.findAllByStudent(student, pageCriteria);
  }

  @Override
  public DeclaredSkillProgress createDeclaredSkillProgress(
      UUID declaredSkillId,
      EExternalSkillType type,
      EDeclaredSkillLevel level,
      String description) {
    Student student = loggedInUserService.getLoggedInStudent();
    requireNotNull("externalSkillId", declaredSkillId);
    requireNotNull("type", type);
    requireNotNull("level", level);
    try {
      checkDescriptionField(description);
      DeclaredSkill declaredSkill =
          declaredSkillSyncService
              .getOrCreateFromExternalSkill(declaredSkillId)
              .orElseThrow(DeclaredSkillNotFoundException::new);
      DeclaredSkillProgress declaredSkillProgress =
          DeclaredSkillProgress.create(student, declaredSkill, level, description);
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
      UUID declaredSkillProgressId, EDeclaredSkillLevel level, String description) {
    checkDescriptionField(description);
    Student student = loggedInUserService.getLoggedInStudent();

    DeclaredSkillProgress declaredSkillProgress =
        declaredSkillProgressRepository
            .findById(declaredSkillProgressId)
            .orElseThrow(DeclaredSkillProgressNotFoundException::new);

    if (!declaredSkillProgress.getStudent().getId().equals(student.getId())) {
      throw new UserNotAuthorizedException();
    }

    declaredSkillProgress.setLevel(level);
    declaredSkillProgress.setDescription(description);

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

    List<Trace> traces =
        traceService.getTracesLinkedWithDeclaredSkillProgress(
            student.getUser(), declaredSkillProgress);

    UUID externalSkillId = declaredSkillProgress.getSkill().getExternalSkillId();
    ExternalSkillDetailsDTO externalSkillDetails =
        externalSkillClient
            .getExternalSkillDetails(externalSkillId)
            .orElse(new ExternalSkillDetailsDTO(externalSkillId, "", List.of(), null));

    return new DeclaredSkillProgressDetails(
        declaredSkillProgress,
        traces.stream()
            .map(
                trace ->
                    new TraceWithProjectNameData(trace, traceService.programNameOfTrace(trace)))
            .toList(),
        externalSkillDetails.categoryPath());
  }

  @Override
  public void unassociateTraces(UUID declaredSkillProgressId, List<UUID> traceIds) {
    Student student = loggedInUserService.getLoggedInStudent();

    DeclaredSkillProgress declaredSkillProgress =
        declaredSkillProgressRepository
            .findById(declaredSkillProgressId)
            .orElseThrow(DeclaredSkillProgressNotFoundException::new);

    if (!declaredSkillProgress.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }

    traceService.unassociateTraces(declaredSkillProgress, traceIds);
  }

  @Override
  public PagedResult<DeclaredSkillProgress> searchDeclaredSkill(
      String keyword, PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    return declaredSkillProgressRepository.findAllByStudent(student, pageCriteria, keyword);
  }

  private static void checkDescriptionField(String description) {
    if (description != null && description.length() > DESCRIPTION_LENGTH_MAX) {
      log.error(
          "Description too long: {} characters (max = " + DESCRIPTION_LENGTH_MAX + ")",
          description.length());
      throw new InvalidDescriptionException(
          "Description exceeds "
              + DESCRIPTION_LENGTH_MAX
              + " characters (actual: "
              + description.length()
              + ")");
    }
  }
}
