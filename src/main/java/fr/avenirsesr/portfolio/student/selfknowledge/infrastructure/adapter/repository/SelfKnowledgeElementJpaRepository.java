package fr.avenirsesr.portfolio.student.selfknowledge.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.student.selfknowledge.infrastructure.adapter.model.SelfKnowledgeElementEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SelfKnowledgeElementJpaRepository
    extends JpaRepository<SelfKnowledgeElementEntity, UUID>,
        JpaSpecificationExecutor<SelfKnowledgeElementEntity> {
  void deleteByStudentAndSelfKnowledgeCategory(
      StudentEntity student, ESelfKnowledgeCategory selfKnowledgeCategory);
}
