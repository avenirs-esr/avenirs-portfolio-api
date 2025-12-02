package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.repository.GenericTranslatedJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeCategoryRepository;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.mapper.SelfKnowledgeCategoryMapper;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryTranslationEntity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.specification.StudentOwnershipSpecification;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SelfKnowledgeCategoryDatabaseRepository
    extends GenericTranslatedJpaRepositoryAdapter<
        SelfKnowledgeCategory, SelfKnowledgeCategoryEntity, SelfKnowledgeCategoryTranslationEntity>
    implements SelfKnowledgeCategoryRepository {

  public SelfKnowledgeCategoryDatabaseRepository(
      SelfKnowledgeCategoryJpaRepository jpaRepository,
      SelfKnowledgeCategoryTranslationJpaRepository translationJpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        translationJpaRepository,
        translationJpaRepository,
        SelfKnowledgeCategoryMapper::fromDomain,
        SelfKnowledgeCategoryMapper::toDomain,
        SelfKnowledgeCategoryMapper::toDomain);
  }

  @Override
  public List<SelfKnowledgeCategory> findAllByStudent(Student student) {
    return findAll(StudentOwnershipSpecification.hasStudent(student, "students"));
  }

  @Override
  public List<SelfKnowledgeCategory> findAllAvailableByStudent(Student student) {
    return findAllByTranslation(
        StudentOwnershipSpecification.hasNotStudent(student, "category.students"));
  }
}
