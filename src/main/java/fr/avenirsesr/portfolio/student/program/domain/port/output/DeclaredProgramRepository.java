package fr.avenirsesr.portfolio.student.program.domain.port.output;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.user.domain.model.Student;

public interface DeclaredProgramRepository extends GenericRepositoryPort<DeclaredProgram> {
  PagedResult<DeclaredProgram> findAllByStudent(
      Student student,
      PageCriteria pageCriteria,
      Boolean isValorized,
      SortCriteria... sortCriterias);
}
