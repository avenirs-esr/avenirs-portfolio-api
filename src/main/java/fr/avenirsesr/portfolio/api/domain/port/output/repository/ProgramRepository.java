package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.Program;
import java.util.List;

public interface ProgramRepository {
  void save(Program program);

  void saveAll(List<Program> programs);
}
