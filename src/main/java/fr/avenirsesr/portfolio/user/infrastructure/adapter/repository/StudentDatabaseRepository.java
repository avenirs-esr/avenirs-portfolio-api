package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.exception.SelfKnowledgeCategoryNotLinkedToStudentException;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

@Repository
public class StudentDatabaseRepository extends GenericJpaRepositoryAdapter<Student, StudentEntity>
    implements StudentRepository {
  private final StudentJpaRepository jpaRepository;

  public StudentDatabaseRepository(StudentJpaRepository repository) {
    super(repository, repository, StudentEntity.class, StudentMapper.INSTANCE);
    this.jpaRepository = repository;
  }

  @Override
  public void addSelfKnowledgeCategories(Student student, List<ESelfKnowledgeCategory> categories) {
    StudentEntity studentEntity =
        jpaRepository.findById(student.getId()).orElseThrow(UserNotFoundException::new);
    List<ESelfKnowledgeCategory> updated =
        Stream.concat(studentEntity.getSelfKnowledgeCategories().stream(), categories.stream())
            .distinct()
            .toList();
    studentEntity.setSelfKnowledgeCategories(new ArrayList<>(updated));
    jpaRepository.save(studentEntity);
  }

  @Override
  public void removeSelfKnowledgeCategory(
      Student student, ESelfKnowledgeCategory selfKnowledgeCategory) {
    StudentEntity studentEntity =
        jpaRepository.findById(student.getId()).orElseThrow(UserNotFoundException::new);
    boolean removed = studentEntity.getSelfKnowledgeCategories().remove(selfKnowledgeCategory);
    if (!removed) {
      throw new SelfKnowledgeCategoryNotLinkedToStudentException();
    }
    jpaRepository.save(studentEntity);
  }
}
