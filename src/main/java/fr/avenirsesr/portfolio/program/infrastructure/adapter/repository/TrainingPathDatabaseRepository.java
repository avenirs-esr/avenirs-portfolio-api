package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.port.output.TrainingPathRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.TrainingPathMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingPathDatabaseRepository
    extends GenericJpaRepositoryAdapter<TrainingPath, TrainingPathEntity>
    implements TrainingPathRepository {
  private final TrainingPathJpaRepository jpaRepository;

  public TrainingPathDatabaseRepository(TrainingPathJpaRepository jpaRepository) {
    super(
        jpaRepository, jpaRepository, TrainingPathMapper::fromDomain, TrainingPathMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<TrainingPath> findAllTrainingPathsByStudents(Student student) {
    return jpaRepository.findAllByStudentId(student.getId()).stream()
        .map(TrainingPathMapper::toDomainWithoutRecursion)
        .collect(Collectors.groupingBy(TrainingPath::getId))
        .values()
        .stream()
        .map(List::getFirst)
        .toList();
  }
}
