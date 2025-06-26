package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.PageInfo;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.SortParam;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
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
  public List<ProgramProgress> findAllByStudent(Student student, ELanguage language) {
    return entityListToDomainList(
        jpaSpecificationExecutor.findAll(
            ProgramProgressSpecification.hasStudent(UserMapper.fromDomain(student))),
        language);
  }

  @Override
  public List<ProgramProgress> findAllByStudent(
      Student student, ELanguage language, SortParam sortParam) {
    Sort sort =
        Sort.by(
            Sort.Direction.fromString(sortParam.getDirection()),
            sortFieldToExactPath(sortParam.getField()));
    return entityListToDomainList(
        jpaSpecificationExecutor.findAll(
            ProgramProgressSpecification.hasStudent(UserMapper.fromDomain(student)), sort),
        language);
  }

  @Override
  public List<ProgramProgress> findAllByStudent(
      Student student, ELanguage language, PageInfo pageInfo) {
    Pageable pageable = PageRequest.of(pageInfo.number(), pageInfo.pageSize());
    return entityListToDomainList(
        jpaSpecificationExecutor
            .findAll(
                ProgramProgressSpecification.hasStudent(UserMapper.fromDomain(student)), pageable)
            .getContent(),
        language);
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
  public List<ProgramProgress> findAllWithoutSkillsByStudent(Student student, ELanguage language) {
    return jpaRepository.findAllByStudentIdAndLang(student.getId(), language).stream()
        .map(
            programProgressDTO ->
                ProgramProgressMapper.toDomainWithoutSkills(programProgressDTO, student, language))
        .toList();
  }

  private List<ProgramProgress> entityListToDomainList(
      List<ProgramProgressEntity> programProgressEntityList, ELanguage language) {
    return programProgressEntityList.stream()
        .map(
            programProgressEntity ->
                ProgramProgressMapper.toDomain(programProgressEntity, language))
        .collect(Collectors.toList());
  }

  private String sortFieldToExactPath(String sortField) {
    return switch (sortField) {
      case "name" -> "program.translations.name";
      case "date" ->
          "createdAt"; // TODO : Sera fonctionnel après ajout de createdAt sur toutes les entités
      default -> "program.translations.name";
    };
  }
}
