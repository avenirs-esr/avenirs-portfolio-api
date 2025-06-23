package fr.avenirsesr.portfolio.api.application.adapter.response;

import fr.avenirsesr.portfolio.api.domain.model.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"data", "page"})
public interface PagedResponse<T> {
  @Schema(description = "List of elements on the current page")
  List<T> data();

  @Schema(description = "Pagination information")
  PageInfo page();
}
