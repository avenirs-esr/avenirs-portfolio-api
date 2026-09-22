package fr.avenirsesr.portfolio.staff.activity.application.adapter.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema()
public record ActivityDraftTargetingUpdateRequest(
    List<UUID> targetInstitutionIds, List<UUID> targetGroupIds) {}
