package fr.avenirsesr.portfolio.student.activity.domain.data;

import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EActivityAssessmentStatus;

public record DeclaredActivityAssessmentDetailsData(
    DeclaredActivity declaredActivity,
    DeclaredActivityAssociationsData associations,
    EActivityAssessmentStatus status) {}
