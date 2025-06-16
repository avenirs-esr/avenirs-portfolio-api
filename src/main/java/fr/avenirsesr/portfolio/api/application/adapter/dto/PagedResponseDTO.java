package fr.avenirsesr.portfolio.api.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"content", "page"})
public record PagedResponseDTO<T>(
    @Schema(description = "List of elements on the current page") List<T> content,
    @Schema(description = "Pagination information") PageDTO page) {
}
