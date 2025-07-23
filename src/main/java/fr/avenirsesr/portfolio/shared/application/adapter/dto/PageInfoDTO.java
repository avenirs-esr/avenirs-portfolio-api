package fr.avenirsesr.portfolio.shared.application.adapter.dto;

import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"pageSize", "totalElements", "totalPages", "page"})
public record PageInfoDTO(int page, int pageSize, long totalElements, int totalPages) {
  public static PageInfoDTO fromDomain(PageInfo pageInfo) {
    return new PageInfoDTO(
        pageInfo.page(),
        pageInfo.pageSize(),
        pageInfo.totalElements(),
        (int) Math.ceil((double) pageInfo.totalElements() / pageInfo.pageSize()));
  }
}
