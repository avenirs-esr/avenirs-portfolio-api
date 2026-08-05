package fr.avenirsesr.portfolio.staff.activity.domain.data;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.Instant;
import java.util.UUID;

public record ActivityStaffOverviewData(
    UUID activityId,
    String title,
    EActivityThematic thematic,
    Staff author,
    EActivityStatus activityStatus,
    Instant updatedAt) {}
