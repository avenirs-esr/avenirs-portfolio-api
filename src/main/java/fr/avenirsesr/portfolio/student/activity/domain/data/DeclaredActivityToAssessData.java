package fr.avenirsesr.portfolio.student.activity.domain.data;

import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;

public record DeclaredActivityToAssessData(
    DeclaredActivity declaredActivity, EDeclaredActivityStatus status) {}
