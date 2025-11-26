package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeCategoryNotLinkedToStudentException;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.mapper.SelfKnowledgeCategoryMapper;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class StudentDatabaseRepository extends GenericJpaRepositoryAdapter<Student, StudentEntity>
    implements StudentRepository {
  private final StudentJpaRepository jpaRepository;

  public StudentDatabaseRepository(StudentJpaRepository repository) {
    super(repository, repository, StudentMapper::fromDomain, StudentMapper::toDomain);
    this.jpaRepository = repository;
  }

  @Override
  public void addSelfKnowledgeCategories(Student student, List<SelfKnowledgeCategory> categories) {
    StudentEntity studentEntity =
        jpaRepository.findById(student.getId()).orElseThrow(UserNotFoundException::new);
    studentEntity
        .getSelfKnowledgeCategories()
        .addAll(categories.stream().map(SelfKnowledgeCategoryMapper::fromDomain).toList());
    jpaRepository.save(studentEntity);
  }

  @Override
  public void removeSelfKnowledgeCategory(
      Student student, SelfKnowledgeCategory selfKnowledgeCategory) {
    StudentEntity studentEntity =
        jpaRepository.findById(student.getId()).orElseThrow(UserNotFoundException::new);
    boolean removed =
        studentEntity
            .getSelfKnowledgeCategories()
            .removeIf(category -> category.getId().equals(selfKnowledgeCategory.getId()));
    if (!removed) {
      throw new SelfKnowledgeCategoryNotLinkedToStudentException();
    }
    jpaRepository.save(studentEntity);
  }
}
