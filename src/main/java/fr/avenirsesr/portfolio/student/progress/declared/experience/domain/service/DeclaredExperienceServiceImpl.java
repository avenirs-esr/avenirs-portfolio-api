package fr.avenirsesr.portfolio.student.progress.declared.experience.domain.service;

import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.application.adapter.exception.RequestContextNotDefinedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
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
    Student student;
    try {
      student = loggedInUserService.getLoggedInStudent();
    } catch (RequestContextNotDefinedException e) {
      student = studentRepository.findById(studentId).orElseThrow(UserNotAuthorizedException::new);
    }

    if (!student.getId().equals(studentId)) {
      throw new UserNotAuthorizedException(
          "Student not authorized. loggedIn student : %s student is provided : %s"
              .formatted(student, studentId));
    }
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
}
