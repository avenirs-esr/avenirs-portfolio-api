package fr.avenirsesr.portfolio.api.application.adapter.response;

import fr.avenirsesr.portfolio.api.domain.model.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"data", "page"})
public record TracesViewResponse(TracesResponse data, PageInfo page) {}
