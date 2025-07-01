package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.PageInfo;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.specification.ProgramProgressSpecification;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class ProgramProgressDatabaseRepository
    extends GenericJpaRepositoryAdapter<ProgramProgress, ProgramProgressEntity>
    implements ProgramProgressRepository {
  private final ProgramProgressJpaRepository jpaRepository;

  public ProgramProgressDatabaseRepository(ProgramProgressJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        ProgramProgressMapper::fromDomain,
        ProgramProgressMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<ProgramProgress> findAllByStudent(Student student) {
    return entityListToDomainList(
        jpaSpecificationExecutor.findAll(
            ProgramProgressSpecification.hasStudent(UserMapper.fromDomain(student))));
  }

  @Override
  public List<ProgramProgress> findAllByStudent(Student student, SortCriteria sortCriteria) {
    Sort sort =
        Sort.by(
            Sort.Direction.fromString(sortCriteria.getOrder().name()),
            sortFieldToExactPath(sortCriteria.getField()));
    return entityListToDomainList(
        jpaSpecificationExecutor.findAll(
            ProgramProgressSpecification.hasStudent(UserMapper.fromDomain(student)), sort));
  }

  @Override
  public List<ProgramProgress> findAllByStudent(Student student, PageInfo pageInfo) {
    Pageable pageable = PageRequest.of(pageInfo.number(), pageInfo.pageSize());
    return entityListToDomainList(
        jpaSpecificationExecutor
            .findAll(
                ProgramProgressSpecification.hasStudent(UserMapper.fromDomain(student)), pageable)
            .getContent());
  }

  @Override
  public List<ProgramProgress> findAllAPCByStudent(Student student) {
    return jpaSpecificationExecutor
        .findAll(
            ProgramProgressSpecification.hasStudent(UserMapper.fromDomain(student))
                .and(ProgramProgressSpecification.isAPC()))
        .stream()
        .map(ProgramProgressMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<ProgramProgress> findAllWithoutSkillsByStudent(Student student) {
    return jpaRepository.findAllByStudentIdAndLang(student.getId()).stream()
        .map(
            programProgressDTO ->
                ProgramProgressMapper.toDomainWithoutRecursion(programProgressDTO, student))
        .toList();
  }

  private List<ProgramProgress> entityListToDomainList(
      List<ProgramProgressEntity> programProgressEntityList) {
    return programProgressEntityList.stream()
        .map(ProgramProgressMapper::toDomain)
        .collect(Collectors.toList());
  }

  private String sortFieldToExactPath(ESortField sortField) {
    return switch (sortField) {
      case ESortField.NAME -> "program.translations.name";
      case ESortField.DATE ->
          "createdAt"; // TODO : Sera fonctionnel après ajout de createdAt sur toutes les entités
    };
  }
}
