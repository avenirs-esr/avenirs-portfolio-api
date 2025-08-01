package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.SkillLevelProgressMapper;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.StudentProgressMapper;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.specification.StudentProgressSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class StudentProgressDatabaseRepository
    extends GenericJpaRepositoryAdapter<StudentProgress, StudentProgressEntity>
    implements StudentProgressRepository {
  private final StudentProgressJpaRepository jpaRepository;

  public StudentProgressDatabaseRepository(StudentProgressJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        StudentProgressMapper::fromDomain,
        StudentProgressMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<StudentProgress> findAllByStudent(Student student) {
    return entityListToDomainList(
        jpaSpecificationExecutor.findAll(
            StudentProgressSpecification.hasStudent(UserMapper.fromDomain(student))));
  }

  @Override
  public List<StudentProgress> findAllAPCByStudent(Student student) {
    return jpaSpecificationExecutor
        .findAll(
            StudentProgressSpecification.hasStudent(UserMapper.fromDomain(student))
                .and(StudentProgressSpecification.isAPC()))
        .stream()
        .map(StudentProgressMapper::toDomain)
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
    UserEntity student = UserMapper.fromDomain(skillLevelProgresses.getFirst().getStudent());
    return jpaSpecificationExecutor
        .findAll(
            StudentProgressSpecification.hasStudent(student)
                .and(
                    StudentProgressSpecification.hasSkillLevelProgresses(
                        skillLevelProgresses.stream()
                            .map(SkillLevelProgressMapper::fromDomain)
                            .toList())))
        .stream()
        .map(StudentProgressMapper::toDomain)
        .collect(Collectors.groupingBy(StudentProgress::getTrainingPath))
        .values()
        .stream()
        .map(List::getFirst)
        .toList();
  }

  private List<StudentProgress> entityListToDomainList(
      List<StudentProgressEntity> trainingPathEntityList) {
    return trainingPathEntityList.stream().map(StudentProgressMapper::toDomain).toList();
  }

  private String sortFieldToExactPath(ESortField sortField) {
    return switch (sortField) {
      case ESortField.NAME -> "trainingPath.program.translations.name";
      case ESortField.DATE -> "createdAt";
    };
  }
}
