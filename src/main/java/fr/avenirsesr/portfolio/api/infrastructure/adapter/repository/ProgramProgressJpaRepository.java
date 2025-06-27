package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.dto.ProgramProgressSummaryDTO;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProgramProgressJpaRepository
    extends JpaRepository<ProgramProgressEntity, UUID>,
        JpaSpecificationExecutor<ProgramProgressEntity> {
  @Query(
      "SELECT new fr.avenirsesr.portfolio.api.infrastructure.adapter.dto.ProgramProgressSummaryDTO("
          + "p.id, pr) "
          + "FROM ProgramProgressEntity p "
          + "JOIN p.program pr "
          + "WHERE p.student.id = :studentId")
  List<ProgramProgressSummaryDTO> findAllByStudentIdAndLang(UUID studentId);
}
