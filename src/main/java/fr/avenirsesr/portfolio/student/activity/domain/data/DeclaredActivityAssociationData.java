package fr.avenirsesr.portfolio.student.activity.domain.data;

import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import java.util.UUID;

public record DeclaredActivityAssociationData(
    UUID associationId, DeclaredActivity declaredActivity, EDeclaredActivityStatus status) {}
