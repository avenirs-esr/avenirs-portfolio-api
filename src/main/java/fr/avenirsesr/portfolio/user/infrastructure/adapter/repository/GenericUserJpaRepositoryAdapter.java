package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.specification.StudentSpecification;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public abstract class GenericUserJpaRepositoryAdapter<
        D extends AvenirsBaseModel, E extends AvenirsBaseEntity>
    extends GenericJpaRepositoryAdapter<D, E> {

  protected GenericUserJpaRepositoryAdapter(
      JpaRepository<E, UUID> jpaRepository,
      JpaSpecificationExecutor<E> jpaSpecificationExecutor,
      Function<D, E> fromDomain,
      Function<E, D> toDomain) {
    super(jpaRepository, jpaSpecificationExecutor, fromDomain, toDomain);
  }

  protected Specification<E> hasStudent(Student student) {
    return StudentSpecification.hasStudent(student);
  }
}
