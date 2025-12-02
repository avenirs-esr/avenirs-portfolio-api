package fr.avenirsesr.portfolio.ams.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSTranslationEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.specification.AMSSpecification;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.common.repository.GenericTranslatedJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.specification.StudentOwnershipSpecification;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class AMSDatabaseRepository
    extends GenericTranslatedJpaRepositoryAdapter<AMS, AMSEntity, AMSTranslationEntity>
    implements AMSRepository {

  public AMSDatabaseRepository(
      AMSJpaRepository repository, AMSTranslationJpaRepository translationRepository) {
    super(
        repository,
        repository,
        translationRepository,
        translationRepository,
        AMSMapper::fromDomain,
        AMSMapper::toDomain,
        AMSMapper::toDomain);
  }

  @Override
  public PagedResult<AMS> findByStudent(Student student, PageCriteria pageCriteria) {
    return findAllBy(
        StudentOwnershipSpecification.hasStudent(student, "ams.student"), pageCriteria);
  }

  @Override
  public PagedResult<AMS> findByStudent(
      Student student, PageCriteria pageCriteria, String keyword) {
    return findAllBy(
        StudentOwnershipSpecification.<AMSTranslationEntity>hasStudent(student)
            .and(AMSSpecification.search(keyword, TranslationUtil.getRequestLanguage())),
        pageCriteria);
  }

  private PagedResult<AMS> findAllBy(
      Specification<AMSTranslationEntity> specification, PageCriteria pageCriteria) {
    return findAllByTranslation(
        specification,
        PageRequest.of(
            pageCriteria.page(),
            pageCriteria.pageSize(),
            Sort.by(Sort.Direction.DESC, "ams.startDate")));
  }

  @Override
  public List<AMS> findAllByStudent(Student student) {
    return findAllByTranslation(StudentOwnershipSpecification.hasStudent(student));
  }
}
