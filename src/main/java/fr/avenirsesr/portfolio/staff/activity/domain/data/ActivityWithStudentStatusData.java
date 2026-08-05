package fr.avenirsesr.portfolio.staff.activity.domain.data;

import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;

public record ActivityWithStudentStatusData(
    Activity activity, boolean isNew, EDeclaredActivityStatus status) {}
