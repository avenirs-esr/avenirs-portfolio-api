package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeElementRepository;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.mapper.SelfKnowledgeCategoryMapper;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.mapper.SelfKnowledgeElementMapper;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeElementEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.specification.SelfKnowledgeElementSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

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
  public PagedResult<SelfKnowledgeElement> findAllByStudentIdAndCategoryId(
      UUID studentId, UUID selfKnowledgeCategoryId, PageCriteria pageCriteria) {
    Specification<SelfKnowledgeElementEntity> spec =
        SelfKnowledgeElementSpecification.hasStudentId(studentId)
            .and(
                SelfKnowledgeElementSpecification.hasSelfKnowledgeCategoryId(
                    selfKnowledgeCategoryId));
    return findAll(spec, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize()));
  }

  @Override
  public void deleteAllByStudentAndCategory(Student student, SelfKnowledgeCategory category) {
    jpaRepository.deleteByStudentAndSelfKnowledgeCategory(
        StudentMapper.INSTANCE.fromDomain(student),
        SelfKnowledgeCategoryMapper.INSTANCE.fromDomain(category));
  }
}
