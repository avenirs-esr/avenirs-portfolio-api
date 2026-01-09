package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericTranslatableJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeCategoryRepository;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.mapper.SelfKnowledgeCategoryMapper;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryTranslationEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.specification.SelfKnowledgeCategorySpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SelfKnowledgeCategoryDatabaseRepository
    extends GenericTranslatableJpaRepositoryAdapter<
        SelfKnowledgeCategory, SelfKnowledgeCategoryTranslationEntity, SelfKnowledgeCategoryEntity>
    implements SelfKnowledgeCategoryRepository {

  private final SelfKnowledgeCategoryJpaRepository jpaRepository;

  public SelfKnowledgeCategoryDatabaseRepository(SelfKnowledgeCategoryJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        SelfKnowledgeCategoryEntity.class,
        SelfKnowledgeCategoryMapper.INSTANCE,
        SelfKnowledgeCategoryTranslationEntity::create,
        SelfKnowledgeCategoryTranslationEntity::update);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<SelfKnowledgeCategory> findAllByStudent(Student student) {
    return findAll(SelfKnowledgeCategorySpecification.hasStudent(student));
  }

  @Override
  public List<SelfKnowledgeCategory> findAllAvailableByStudent(Student student) {
    return findAll(SelfKnowledgeCategorySpecification.hasNotStudent(student));
  }

  @Override
  public List<SelfKnowledgeCategory> findAllMandatory() {
    return findAll(SelfKnowledgeCategorySpecification.isMandatory());
  }
}
