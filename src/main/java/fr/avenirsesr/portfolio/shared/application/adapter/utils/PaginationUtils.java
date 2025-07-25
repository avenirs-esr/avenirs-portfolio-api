package fr.avenirsesr.portfolio.shared.application.adapter.utils;

import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import java.util.List;

public class PaginationUtils {
  public static <T> PagedResult<T> paginate(List<T> data, PageCriteria pageCriteria) {
    int totalElements = data.size();
    int page = pageCriteria.page() - 1;
    int start = Math.min(page * pageCriteria.pageSize(), totalElements);
    int end = Math.min(start + pageCriteria.pageSize(), totalElements);
    List<T> paginatedSkills = data.subList(start, end);
    return new PagedResult<>(
        paginatedSkills, new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), data.size()));
  }
}
