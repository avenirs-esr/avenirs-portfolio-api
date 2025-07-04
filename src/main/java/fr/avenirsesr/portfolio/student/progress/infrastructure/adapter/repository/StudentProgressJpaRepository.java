package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.dto.StudentTrainingPathSummaryDTO;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.StudentProgressEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface StudentProgressJpaRepository
    extends JpaRepository<StudentProgressEntity, UUID>,
        JpaSpecificationExecutor<StudentProgressEntity> {
  @Query(
      "SELECT new"
          + " fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.dto.StudentTrainingPathSummaryDTO(p.id,"
          + " pr) FROM StudentProgressEntity s JOIN s.trainingPath p JOIN p.program pr WHERE s.student.id = :studentId")
  List<StudentTrainingPathSummaryDTO> findAllByStudentIdAndLang(UUID studentId);
}
