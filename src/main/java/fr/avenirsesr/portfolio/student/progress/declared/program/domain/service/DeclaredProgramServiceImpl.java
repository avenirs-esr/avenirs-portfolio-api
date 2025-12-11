package fr.avenirsesr.portfolio.student.progress.declared.program.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.*;

import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
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

    requireNotBlankAndMaxLength("title", title, 80);
    requireNotBlankAndMaxLength("organization", organization, 50);
    validateOptionalTextMaxLength("description", description, 400);
    validateOptionalTextMaxLength("result", result, 50);
    validateOptionalTextMaxLength("sourceOfInformation", sourceOfInformation, 200);
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
      EProgramStatus status,
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
}
