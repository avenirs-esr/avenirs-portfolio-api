package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.dto.StudentTrainingPathSummaryDTO;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TrainingPathJpaRepository
    extends JpaRepository<TrainingPathEntity, UUID>, JpaSpecificationExecutor<TrainingPathEntity> {
  @Query(
      "SELECT new"
          + " fr.avenirsesr.portfolio.program.infrastructure.adapter.dto.StudentTrainingPathSummaryDTO(p.id,"
          + " pr) FROM StudentProgressEntity s JOIN s.trainingPath p JOIN p.program pr WHERE"
          + " s.student.id = :studentId")
  List<StudentTrainingPathSummaryDTO> findAllByStudentId(UUID studentId);
}
