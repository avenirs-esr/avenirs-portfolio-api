package fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.enums.EExperienceType;
import java.time.LocalDate;
import java.util.UUID;

public record DeclaredExperienceCreationData(
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
    LocalDate endDate) {}
