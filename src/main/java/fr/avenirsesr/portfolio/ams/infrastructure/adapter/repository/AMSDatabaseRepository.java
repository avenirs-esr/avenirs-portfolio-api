package fr.avenirsesr.portfolio.ams.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.specification.AMSSpecification;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class AMSDatabaseRepository extends GenericUserJpaRepositoryAdapter<AMS, AMSEntity>
    implements AMSRepository {

  public AMSDatabaseRepository(AMSJpaRepository repository) {
    super(repository, repository, AMSEntity.class, AMSMapper.INSTANCE);
  }

  @Override
  public PagedResult<AMS> findByStudent(Student student, PageCriteria pageCriteria) {
    return findAllBy(hasStudent(student), pageCriteria);
  }

  @Override
  public PagedResult<AMS> findByStudent(
      Student student, PageCriteria pageCriteria, String keyword) {
    return findAllBy(
        hasStudent(student)
            .and(AMSSpecification.search(keyword, TranslationUtil.getRequestLanguage())),
        pageCriteria);
  }

  private PagedResult<AMS> findAllBy(
      Specification<AMSEntity> specification, PageCriteria pageCriteria) {
    return findAll(
        specification,
        PageRequest.of(
            pageCriteria.page(),
            pageCriteria.pageSize(),
            Sort.by(Sort.Direction.DESC, "startDate")));
  }

  @Override
  public List<AMS> findAllByStudent(Student student) {
    return findAll(hasStudent(student));
  }
}
