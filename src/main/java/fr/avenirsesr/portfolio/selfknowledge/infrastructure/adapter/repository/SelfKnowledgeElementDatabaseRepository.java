package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeElementRepository;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.mapper.SelfKnowledgeElementMapper;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeElementEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.specification.SelfKnowledgeElementSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SelfKnowledgeElementDatabaseRepository
    extends GenericJpaRepositoryAdapter<SelfKnowledgeElement, SelfKnowledgeElementEntity>
    implements SelfKnowledgeElementRepository {

  private final SelfKnowledgeElementJpaRepository jpaRepository;

  public SelfKnowledgeElementDatabaseRepository(SelfKnowledgeElementJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        SelfKnowledgeElementEntity.class,
        SelfKnowledgeElementMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public PagedResult<SelfKnowledgeElement> findAllByStudentIdAndCategories(
      UUID studentId,
      List<ESelfKnowledgeCategory> selfKnowledgeCategories,
      PageCriteria pageCriteria,
      Boolean isValorized) {
    Specification<SelfKnowledgeElementEntity> spec =
        SelfKnowledgeElementSpecification.hasStudentId(studentId)
            .and(
                SelfKnowledgeElementSpecification.hasSelfKnowledgeCategoryIn(
                    selfKnowledgeCategories))
            .and(SelfKnowledgeElementSpecification.isValorized(isValorized));
    return findAll(spec, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize()));
  }

  @Override
  @Transactional
  public void deleteAllByStudentAndCategory(
      Student student, ESelfKnowledgeCategory selfKnowledgeCategory) {
    jpaRepository.deleteByStudentAndSelfKnowledgeCategory(
        StudentMapper.INSTANCE.fromDomain(student), selfKnowledgeCategory);
  }
}
