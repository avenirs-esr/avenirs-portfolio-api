package fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.output.DeclaredProgramRepository;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.mapper.DeclaredProgramMapper;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.model.DeclaredProgramEntity;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.specification.DeclaredProgramSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import java.util.Arrays;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class DeclaredProgramDatabaseRepository
    extends GenericUserJpaRepositoryAdapter<DeclaredProgram, DeclaredProgramEntity>
    implements DeclaredProgramRepository {
  public DeclaredProgramDatabaseRepository(DeclaredProgramJpaRepository jpaRepository) {
    super(
        jpaRepository, jpaRepository, DeclaredProgramEntity.class, DeclaredProgramMapper.INSTANCE);
  }

  @Override
  public PagedResult<DeclaredProgram> findAllByStudent(
      Student student, PageCriteria pageCriteria, SortCriteria... sortCriterias) {
    return findAll(
        hasStudent(student),
        PageRequest.of(
            pageCriteria.page(),
            pageCriteria.pageSize(),
            Arrays.stream(sortCriterias)
                .map(DeclaredProgramSpecification::toSort)
                .reduce(Sort.unsorted(), Sort::and)));
  }
}
