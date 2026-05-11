package fr.avenirsesr.portfolio.activity.application.adapter.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"draftId"})
public record ActivityDraftUpdateResponse(UUID draftId) {}
