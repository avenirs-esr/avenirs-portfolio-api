package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.specification.StudentSpecification;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public abstract class GenericUserJpaRepositoryAdapter<
        D extends AvenirsBaseModel, E extends AvenirsBaseEntity>
    extends GenericJpaRepositoryAdapter<D, E> {

  protected GenericUserJpaRepositoryAdapter(
      JpaRepository<E, UUID> jpaRepository,
      JpaSpecificationExecutor<E> jpaSpecificationExecutor,
      Class<E> entityClass,
      Mapper<E, D> mapper) {
    super(jpaRepository, jpaSpecificationExecutor, entityClass, mapper);
  }

  protected Specification<E> hasStudent(Student student) {
    return StudentSpecification.hasStudent(student);
  }
}
