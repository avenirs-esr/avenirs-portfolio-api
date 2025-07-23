package fr.avenirsesr.portfolio.shared.domain.model;

public record PageCriteria(Integer page, Integer pageSize) {
  private static final Integer MAX_PAGE_SIZE = 12;
  private static final PageCriteria DEFAULT_PAGE_CRITERIA = new PageCriteria(1, 8);

  public PageCriteria(Integer page, Integer pageSize) {
    this.page = page == null ? DEFAULT_PAGE_CRITERIA.page : Math.max(1, page);
    this.pageSize =
        pageSize == null ? DEFAULT_PAGE_CRITERIA.pageSize : Math.min(MAX_PAGE_SIZE, pageSize);
  }
}
