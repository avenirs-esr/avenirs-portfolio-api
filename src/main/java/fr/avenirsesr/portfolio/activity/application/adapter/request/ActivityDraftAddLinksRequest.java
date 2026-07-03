package fr.avenirsesr.portfolio.activity.application.adapter.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"links"})
public record ActivityDraftAddLinksRequest(List<String> links) {}
