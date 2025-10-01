package fr.avenirsesr.portfolio.trace.domain.port.output.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericDeletableRepositoryPort;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.TraceFilter;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.util.List;

public interface TraceRepository extends GenericDeletableRepositoryPort<Trace> {
  List<Trace> findLastsOf(User user, int limit);

  PagedResult<Trace> findAll(
      User user,
      String keyword,
      TraceFilter filter,
      DateFilter dateFilter,
      PageCriteria pageCriteria);

  List<Trace> findAll(User user, boolean isAssociated);

  List<Trace> linkedWith(AMS ams);

  List<Trace> linkedWith(SkillLevelProgress skillLevelProgress);
}
