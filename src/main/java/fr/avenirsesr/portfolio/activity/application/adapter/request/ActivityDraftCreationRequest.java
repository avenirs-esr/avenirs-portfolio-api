package fr.avenirsesr.portfolio.activity.application.adapter.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"title"})
public record ActivityDraftCreationRequest(String title) {}
