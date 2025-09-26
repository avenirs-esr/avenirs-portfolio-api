package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.student.progress.domain.exception.SkillLevelNotFoundException;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.SkillLevelProgressMapper;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.specification.SkillLevelProgressSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class SkillLevelProgressDatabaseRepository
    extends GenericJpaRepositoryAdapter<SkillLevelProgress, SkillLevelProgressEntity>
    implements SkillLevelProgressRepository {
  private final SkillLevelProgressJpaRepository jpaRepository;

  public SkillLevelProgressDatabaseRepository(SkillLevelProgressJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        SkillLevelProgressMapper::fromDomain,
        SkillLevelProgressMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<SkillLevelProgress> linkedWith(AMS ams) {
    return jpaSpecificationExecutor
        .findAll(SkillLevelProgressSpecification.linkedTo(AMSMapper.fromDomain(ams)))
        .stream()
        .map(SkillLevelProgressMapper::toDomain)
        .toList();
  }

  @Override
  public List<SkillLevelProgress> findAllByStudent(Student student) {
    return jpaSpecificationExecutor.findAll(SkillLevelProgressSpecification.with(student)).stream()
        .map(SkillLevelProgressMapper::toDomain)
        .toList();
  }

  @Override
  public List<SkillLevelProgress> findAllByStudentAndSkillId(Student student, UUID skillId) {
    List<SkillLevelProgress> skillLevelProgresses =
        Optional.of(
                jpaSpecificationExecutor.findAll(
                    SkillLevelProgressSpecification.with(student, skillId)))
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new SkillLevelNotFoundException(skillId.toString()))
            .stream()
            .map(SkillLevelProgressMapper::toDomain)
            .toList();

    return skillLevelProgresses;
  }

  @Override
  public PagedResult<SkillLevelProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria, String keyword) {
    var language = TranslationUtil.getRequestLanguage();

    Page<SkillLevelProgressEntity> pageResult =
        jpaSpecificationExecutor.findAll(
            SkillLevelProgressSpecification.with(student)
                .and(SkillLevelProgressSpecification.search(keyword, language)),
            PageRequest.of(
                pageCriteria.page(),
                pageCriteria.pageSize(),
                Sort.by(Sort.Direction.DESC, "startDate")));

    var content = pageResult.stream().map(SkillLevelProgressMapper::toDomain).toList();

    return new PagedResult<>(
        content,
        new PageInfo(
            pageResult.getPageable().getPageNumber(),
            pageResult.getPageable().getPageSize(),
            pageResult.getTotalElements()));
  }
}
