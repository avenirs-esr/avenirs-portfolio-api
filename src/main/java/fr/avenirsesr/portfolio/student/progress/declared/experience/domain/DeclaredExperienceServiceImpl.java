package fr.avenirsesr.portfolio.student.progress.declared.experience.domain;

import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.EExperienceType;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.input.service.DeclaredExperienceService;
import java.time.LocalDate;
import java.util.UUID;

public class DeclaredExperienceServiceImpl implements DeclaredExperienceService {

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
    return null;
  }
}
