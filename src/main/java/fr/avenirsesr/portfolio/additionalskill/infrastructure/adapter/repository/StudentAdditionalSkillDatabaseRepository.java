package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.exception.StudentAdditionalSkillConflictException;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.StudentAdditionalSkillRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.StudentAdditionalSkillEntity;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StudentAdditionalSkillDatabaseRepository implements StudentAdditionalSkillRepository {
  private final StudentAdditionalSkillJpaRepository jpaRepository;

  public StudentAdditionalSkillDatabaseRepository(
      StudentAdditionalSkillJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  public void saveAllEntities(List<StudentAdditionalSkillEntity> entities) {
    if (entities != null && !entities.isEmpty()) {
      jpaRepository.saveAll(entities);
    }
  }

  @Override
  public void saveAdditionalSkill(
      Student student,
      String additionalSkillId,
      EAdditionalSkillType type,
      ESkillLevelStatus level) {
    StudentAdditionalSkillEntity entity =
        StudentAdditionalSkillEntity.create(
            UserMapper.fromDomain(student), additionalSkillId, type, level);
    try {
      jpaRepository.save(entity);
    } catch (Exception e) {
      if (isUniqueConstraintViolation(e)) {
        throw new StudentAdditionalSkillConflictException();
      }
      throw e;
    }
  }

  private boolean isUniqueConstraintViolation(Exception e) {
    Throwable cause = e;
    while (cause != null) {
      if (cause.getMessage().contains("duplicate key")) {
        return true;
      }
      cause = cause.getCause();
    }
    return false;
  }
}
