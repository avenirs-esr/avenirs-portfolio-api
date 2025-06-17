package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.PagedResult;
import fr.avenirsesr.portfolio.api.domain.model.PaginationInfo;
import fr.avenirsesr.portfolio.api.domain.model.User;

public interface AMSService {
  PagedResult<AMS> findUserAmsWithPagination(User user, int page, int size);
  
  PaginationInfo createPaginationInfo(int page, int size, long totalElements, int totalPages);
}
