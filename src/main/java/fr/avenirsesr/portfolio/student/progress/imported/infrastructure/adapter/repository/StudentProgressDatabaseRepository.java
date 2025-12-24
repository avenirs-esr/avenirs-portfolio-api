package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper.StudentProgressMapper;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.specification.StudentProgressSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class StudentProgressDatabaseRepository
    extends GenericUserJpaRepositoryAdapter<StudentProgress, StudentProgressEntity>
    implements StudentProgressRepository {

  @PersistenceContext private EntityManager em;

  public StudentProgressDatabaseRepository(StudentProgressJpaRepository jpaRepository) {
    super(
        jpaRepository, jpaRepository, StudentProgressEntity.class, StudentProgressMapper.INSTANCE);
  }

  @Override
  public List<StudentProgress> findAllByStudent(Student student) {

    var graph =
        FetchGraph.init()
            .fetch("student")
            .add("trainingPath")
            .add("program")
            .fetch("translations")
            .add("institution")
            .fetch("translations")
            .root()
            .add("skillLevels")
            .root()
            .from("skillLevels")
            .add("student")
            .fetch("user")
            .root()
            .from("skillLevels")
            .add("skillLevel")
            .fetch("translations")
            .add("skill")
            .fetch("translations")
            .root();

    return findAll(hasStudent(student), graph);
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
