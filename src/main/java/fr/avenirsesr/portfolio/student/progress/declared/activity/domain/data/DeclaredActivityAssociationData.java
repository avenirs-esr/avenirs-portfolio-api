package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import java.util.UUID;

public record DeclaredActivityAssociationData(
    UUID associationId, DeclaredActivity declaredActivity, EDeclaredActivityStatus status) {}
