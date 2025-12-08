package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper.StudentProgressMapper;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.specification.StudentProgressSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class StudentProgressDatabaseRepository
    extends GenericUserJpaRepositoryAdapter<StudentProgress, StudentProgressEntity>
    implements StudentProgressRepository {

  public StudentProgressDatabaseRepository(StudentProgressJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        StudentProgressMapper::fromDomain,
        StudentProgressMapper::toDomain);
  }

  @Override
  public List<StudentProgress> findAllByStudent(Student student) {
    return findAll(hasStudent(student));
  }

  @Override
  public List<StudentProgress> findAllAPCByStudent(Student student) {
    return findAll(hasStudent(student).and(StudentProgressSpecification.isAPC())).stream()
        .collect(Collectors.groupingBy(StudentProgress::getTrainingPath))
        .values()
        .stream()
        .map(List::getFirst)
        .toList();
  }

  @Override
  public List<StudentProgress> findStudentProgressesBySkillLevelProgresses(
      List<SkillLevelProgress> skillLevelProgresses) {
    if (skillLevelProgresses.isEmpty()) {
      return List.of();
    }
    return findAll(
            hasStudent(skillLevelProgresses.getFirst().getStudent())
                .and(StudentProgressSpecification.hasSkillLevelProgresses(skillLevelProgresses)))
        .stream()
        .collect(Collectors.groupingBy(StudentProgress::getTrainingPath))
        .values()
        .stream()
        .map(List::getFirst)
        .toList();
  }
}
