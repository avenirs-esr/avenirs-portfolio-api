package fr.avenirsesr.portfolio.activity.domain.data;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;

public record ActivityWithStudentStatusData(
    Activity activity, boolean isNew, EDeclaredActivityStatus status) {}
