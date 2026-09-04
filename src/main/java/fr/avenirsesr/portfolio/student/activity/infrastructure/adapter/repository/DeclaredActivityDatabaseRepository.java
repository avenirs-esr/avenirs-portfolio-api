package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.repository;

import static fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.specification.DeclaredActivitySpecification.hasActivityIdInAndStudent;

import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.mapper.DeclaredActivityMapper;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.specification.DeclaredActivitySpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class DeclaredActivityDatabaseRepository
    extends GenericUserJpaRepositoryAdapter<DeclaredActivity, DeclaredActivityEntity>
    implements DeclaredActivityRepository {
  private final DeclaredActivityJpaRepository jpaRepository;

  public DeclaredActivityDatabaseRepository(DeclaredActivityJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        DeclaredActivityEntity.class,
        DeclaredActivityMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<DeclaredActivity> findAllByStudent(Student student, FetchGraph fetchGraph) {
    return findAll(hasStudent(student), fetchGraph);
  }

  @Override
  public PagedResult<DeclaredActivity> findAllByStudent(
      Student student, String keyword, PageCriteria pageCriteria, FetchGraph fetchGraph) {
    var specification = hasStudent(student).and(DeclaredActivitySpecification.search(keyword));
    return findAll(
        specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize()), fetchGraph);
  }

  @Override
  public List<DeclaredActivity> findAllByActivityIdAndStudent(
      List<UUID> activityIds, Student student, FetchGraph fetchGraph) {
    if (activityIds == null || activityIds.isEmpty()) {
      return List.of();
    }

    return findAll(hasActivityIdInAndStudent(activityIds, student), fetchGraph);
  }

  @Override
  public List<DeclaredActivity> findAllNotCompletedActivitiesByIds(
      List<UUID> activityIds, FetchGraph fetchGraph) {
    var specification = DeclaredActivitySpecification.isNotCompleted();
    return findAllById(activityIds, specification, fetchGraph);
  }

  @Override
  public PagedResult<DeclaredActivity> findStudentActivitiesByProgressAndDate(
      Student student, PageCriteria pageCriteria, FetchGraph fetchGraph) {
    var sort =
        Sort.by(Sort.Direction.ASC, "isFinishedOrder")
            .and(Sort.by(Sort.Direction.DESC, "updatedAt"));
    return findAll(
        hasStudent(student),
        PageRequest.of(pageCriteria.page(), pageCriteria.pageSize(), sort),
        fetchGraph);
  }

  @Override
  public Optional<DeclaredActivity> findByActivity(Student student, Activity activity) {
    return jpaRepository
        .findByStudentIdAndActivityId(student.getId(), activity.getId())
        .map(DeclaredActivityMapper.INSTANCE::toDomain);
  }

  @Override
  public int countEnrolledByActivity(Activity activity) {
    return jpaRepository.countByActivityIdAndUnsubscribedAtIsNull(activity.getId());
  }

  @Override
  public List<DeclaredActivity> findAllEnrolledByActivity(
      Activity activity, FetchGraph fetchGraph) {
    return findAll(
        DeclaredActivitySpecification.hasActivityId(activity.getId())
            .and(DeclaredActivitySpecification.isNotUnsubscribed()),
        fetchGraph);
  }
}
