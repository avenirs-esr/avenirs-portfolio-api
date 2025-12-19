package fr.avenirsesr.portfolio.student.progress.declared.program.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.*;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.exception.DeclaredProgramNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EPeriodStatus;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.input.DeclaredProgramService;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.output.DeclaredProgramRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class DeclaredProgramServiceImpl implements DeclaredProgramService {
  private final StudentRepository studentRepository;
  private final DeclaredProgramRepository declaredProgramRepository;
  private final LoggedInUserService loggedInUserService;

  private DeclaredProgram create(
      Student student,
      EProgramStatus status,
      String title,
      String description,
      String organization,
      String result,
      String sourceOfInformation,
      String link,
      LocalDate startDate,
      LocalDate endDate) {

    requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
    requireNotBlankAndMaxLength("organization", organization, ORGANIZATION_LENGTH);
    validateOptionalTextMaxLength("description", description, DESCRIPTION_LENGTH);
    validateOptionalTextMaxLength("result", result, RESULT_LENGTH);
    validateOptionalTextMaxLength(
        "sourceOfInformation", sourceOfInformation, SOURCE_OF_INFORMATION_LENGTH);
    requireNotNull("startDate", startDate);
    validateDateOrder(startDate, endDate);

    DeclaredProgram declaredProgram =
        DeclaredProgram.create(
            student,
            status,
            title,
            description,
            organization,
            result,
            sourceOfInformation,
            link,
            startDate,
            endDate);

    return declaredProgramRepository.save(declaredProgram);
  }

  @Override
  public DeclaredProgram create(
      UUID studentId,
      EProgramStatus status,
      String title,
      String description,
      String organization,
      String result,
      String sourceOfInformation,
      String link,
      LocalDate startDate,
      LocalDate endDate) {
    Student student =
        studentRepository.findById(studentId).orElseThrow(UserNotAuthorizedException::new);
    return create(
        student,
        status,
        title,
        description,
        organization,
        result,
        sourceOfInformation,
        link,
        startDate,
        endDate);
  }

  @Override
  public DeclaredProgram create(
      String title,
      String description,
      String organization,
      String result,
      String sourceOfInformation,
      String link,
      LocalDate startDate,
      LocalDate endDate) {
    Student student = loggedInUserService.getLoggedInStudent();
    return create(
        student,
        getProgramStatus(startDate, endDate),
        title,
        description,
        organization,
        result,
        sourceOfInformation,
        link,
        startDate,
        endDate);
  }

  @Override
  public DeclaredProgram getById(UUID declaredProgramId) {
    DeclaredProgram declaredProgram =
        declaredProgramRepository
            .findById(declaredProgramId)
            .orElseThrow(DeclaredProgramNotFoundException::new);
    Student student = loggedInUserService.getLoggedInStudent();
    if (!declaredProgram.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }
    return declaredProgram;
  }

  @Override
  public PagedResult<DeclaredProgram> getDeclaredPrograms(PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    return declaredProgramRepository.findAllByStudent(student, pageCriteria);
  }

  private EProgramStatus getProgramStatus(LocalDate startDate, LocalDate endDate) {
    EPeriodStatus periodStatus = EPeriodStatus.of(startDate, endDate);
    return switch (periodStatus) {
      case EPeriodStatus.DURING -> EProgramStatus.IN_PROGRESS;
      case EPeriodStatus.AFTER -> EProgramStatus.NOT_STARTED;
      case EPeriodStatus.BEFORE -> EProgramStatus.COMPLETED;
    };
  }
}
