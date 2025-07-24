package fr.avenirsesr.portfolio.shared.application.adapter.response;

import fr.avenirsesr.portfolio.shared.application.adapter.dto.PageInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"data", "page"})
public record PagedResponse<T>(List<T> data, PageInfoDTO page) {}
