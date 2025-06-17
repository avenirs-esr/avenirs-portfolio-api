package fr.avenirsesr.portfolio.api.domain.model;

import java.util.List;
import lombok.Getter;

@Getter
public class PagedResult<T> {
  private final List<T> content;
  private final long totalElements;
  private final int totalPages;

  public PagedResult(List<T> content, long totalElements, int totalPages) {
    this.content = content;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
  }
}
