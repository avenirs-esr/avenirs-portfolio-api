package fr.avenirsesr.portfolio.student.progress.declared.experience.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.*;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.output.repository.DeclaredExperienceRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DeclaredExperienceServiceImpl implements DeclaredExperienceService {

  private final LoggedInUserService loggedInUserService;
  private final DeclaredExperienceRepository experienceRepository;
  private final StudentRepository studentRepository;

  @Override
  public DeclaredExperience create(
      UUID studentId,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      LocalDate startDate,
      LocalDate endDate) {
    Student student =
        studentRepository.findById(studentId).orElseThrow(UserNotAuthorizedException::new);
    return create(
        student,
        title,
        experienceType,
        organization,
        activitySector,
        location,
        description,
        sourceOfInformation,
        summary,
        externalLink,
        startDate,
        endDate);
  }

  @Override
  public DeclaredExperience create(
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      LocalDate startDate,
      LocalDate endDate) {
    return create(
        loggedInUserService.getLoggedInStudent(),
        title,
        experienceType,
        organization,
        activitySector,
        location,
        description,
        sourceOfInformation,
        summary,
        externalLink,
        startDate,
        endDate);
  }

  private DeclaredExperience create(
      Student student,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      LocalDate startDate,
      LocalDate endDate) {
    log.info("DeclaredExperience creation for {}", student);

    requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
    requireNotBlankAndMaxLength("organization", organization, ORGANIZATION_LENGTH);
    validateOptionalTextMaxLength("activitySector", activitySector, ACTIVITY_SECTOR_LENGTH);
    validateOptionalTextMaxLength("location", location, LOCATION_LENGTH);
    validateOptionalTextMaxLength(
        "sourceOfInformation", sourceOfInformation, SOURCE_OF_INFORMATION_LENGTH);
    validateOptionalTextMaxLength("description", description, DESCRIPTION_LENGTH);
    validateOptionalTextMaxLength("summary", summary, SUMMARY_LENGTH);
    requireNotNull("startDate", startDate);
    validateDateOrder(startDate, endDate);

    var experience =
        DeclaredExperience.create(
            student,
            title,
            experienceType,
            organization,
            activitySector,
            location,
            description,
            sourceOfInformation,
            summary,
            externalLink,
            startDate,
            endDate);

    experience = experienceRepository.save(experience);
    log.info("{} has been created", experience);
    return experience;
  }

  @Override
  public DeclaredExperience get(UUID experienceId) {
    Student student = loggedInUserService.getLoggedInStudent();
    log.info("Get experienceId {} by {}", experienceId, student);

    var experience =
        experienceRepository
            .findById(experienceId)
            .orElseThrow(DeclaredExperienceNotFoundException::new);

    if (!experience.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }
    return experience;
  }

  @Override
  public PagedResult<DeclaredExperience> getView(PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    log.info("Get experience view by {}", student);

    return experienceRepository.findAllByStudent(student, pageCriteria);
  }
}
