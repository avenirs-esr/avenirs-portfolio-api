package fr.avenirsesr.portfolio.staff.activity.application.adapter.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"authorizations"})
public record ActivityAuthorizationsRequest(List<StaffAuthorizationRequest> authorizations) {}
