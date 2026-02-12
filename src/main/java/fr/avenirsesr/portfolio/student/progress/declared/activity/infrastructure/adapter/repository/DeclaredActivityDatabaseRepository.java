package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.mapper.DeclaredActivityMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DeclaredActivityDatabaseRepository
    extends GenericUserJpaRepositoryAdapter<DeclaredActivity, DeclaredActivityEntity>
    implements DeclaredActivityRepository {
  private final DeclaredActivityJpaRepository jpaRepository;

  public DeclaredActivityDatabaseRepository(DeclaredActivityJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        DeclaredActivityEntity.class,
        DeclaredActivityMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<DeclaredActivity> findAllByStudent(Student student, FetchGraph fetchGraph) {
    return findAll(hasStudent(student), fetchGraph);
  }
}
