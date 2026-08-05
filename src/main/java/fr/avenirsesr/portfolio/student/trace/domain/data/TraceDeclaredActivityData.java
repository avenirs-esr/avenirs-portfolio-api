package fr.avenirsesr.portfolio.student.trace.domain.data;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import java.util.UUID;

public record TraceDeclaredActivityData(
    UUID activityId, String activityTitle, EDeclaredActivityStatus activityStatus) {}
