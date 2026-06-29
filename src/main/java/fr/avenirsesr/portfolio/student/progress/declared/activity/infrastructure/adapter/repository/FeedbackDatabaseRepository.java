package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.mapper.DeclaredSkillMapper;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.repository.DeclaredSkillJpaRepository;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.FileMapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.repository.FileJpaRepository;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.FeedbackRepository;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.mapper.FeedbackMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.AssociationsJson;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.FeedbackEntity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.specification.FeedbackSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.StudentJpaRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.UserJpaRepository;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class FeedbackDatabaseRepository
    extends GenericJpaRepositoryAdapter<Feedback, FeedbackEntity> implements FeedbackRepository {

  private final FeedbackJpaRepository jpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final FileJpaRepository fileJpaRepository;
  private final StudentJpaRepository studentJpaRepository;
  private final DeclaredSkillJpaRepository declaredSkillJpaRepository;

  public FeedbackDatabaseRepository(
      FeedbackJpaRepository jpaRepository,
      UserJpaRepository userJpaRepository,
      FileJpaRepository fileJpaRepository,
      StudentJpaRepository studentJpaRepository,
      DeclaredSkillJpaRepository declaredSkillJpaRepository) {
    super(jpaRepository, jpaRepository, FeedbackEntity.class, FeedbackMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
    this.userJpaRepository = userJpaRepository;
    this.fileJpaRepository = fileJpaRepository;
    this.studentJpaRepository = studentJpaRepository;
    this.declaredSkillJpaRepository = declaredSkillJpaRepository;
  }

  // ── save ────────────────────────────────────────────────────────────

  @Override
  public Feedback save(Feedback domain) {
    FeedbackEntity saved = jpaRepository.save(FeedbackMapper.INSTANCE.fromDomain(domain));
    return toDomainWithDependencies(saved);
  }

  @Override
  public List<Feedback> saveAll(List<Feedback> domains) {
    List<FeedbackEntity> saved =
        jpaRepository.saveAll(domains.stream().map(FeedbackMapper.INSTANCE::fromDomain).toList());
    return saved.stream().map(this::toDomainWithDependencies).toList();
  }

  // ── find ────────────────────────────────────────────────────────────

  @Override
  public Optional<Feedback> findById(UUID id) {
    return jpaRepository.findById(id).map(this::toDomainWithDependencies);
  }

  @Override
  public Optional<Feedback> findById(UUID id, FetchGraph fetchGraph) {
    return findById(id);
  }

  @Override
  public List<Feedback> findAllById(List<UUID> ids) {
    return jpaRepository.findAllById(ids).stream().map(this::toDomainWithDependencies).toList();
  }

  @Override
  public List<Feedback> findAll() {
    return jpaRepository.findAll().stream().map(this::toDomainWithDependencies).toList();
  }

  @Override
  public List<Feedback> findAllByDeclaredActivityId(
      UUID declaredActivityId, EFeedbackStatus status) {
    var spec =
        FeedbackSpecification.hasDeclaredActivityId(declaredActivityId)
            .and(FeedbackSpecification.hasStatus(status));
    var sort = Sort.by("createdAt").descending();
    return jpaRepository.findAll(spec, sort).stream().map(this::toDomainWithDependencies).toList();
  }

  @Override
  public PagedResult<Feedback> findByStaff(
      UUID staffId, EFeedbackStatus statusFilter, UUID activityId, PageCriteria pageCriteria) {
    var specification =
        FeedbackSpecification.hasStaffAuthor(staffId)
            .and(FeedbackSpecification.hasStatus(statusFilter))
            .and(FeedbackSpecification.hasActivityId(activityId));
    // Status strings sort alphabetically in the required priority order:
    // "IN_PROCESS" < "NEW" < "SUBMITTED"
    var sort = Sort.by("status").ascending().and(Sort.by("createdAt").ascending());
    return findAll(
        specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize(), sort));
  }

  // ── pagination ──────────────────────────────────────────────────────

  @Override
  protected PagedResult<Feedback> toPagedResult(Page<FeedbackEntity> page) {
    var content = page.getContent().stream().map(this::toDomainWithDependencies).toList();
    return new PagedResult<>(
        content,
        new PageInfo(
            page.getPageable().getPageNumber(),
            page.getPageable().getPageSize(),
            page.getTotalElements()));
  }

  @Override
  public List<Feedback> findLatestFeedbacksByStaffAndActivityForEachStudent(
      UUID staffId, UUID activityId) {
    var specification =
        FeedbackSpecification.hasStaffAuthor(staffId)
            .and(FeedbackSpecification.hasActivityId(activityId));

    var sort = Sort.by("createdAt").descending();

    return jpaRepository.findAll(specification, sort).stream()
        .map(this::toDomainWithDependencies)
        .collect(
            Collectors.toMap(
                feedback -> feedback.getDeclaredActivity().getStudent().getId(),
                Function.identity(),
                (existing, ignored) -> existing,
                LinkedHashMap::new))
        .values()
        .stream()
        .toList();
  }

  @Override
  public List<UUID> findDeclaredActivityIdsHavingActiveFeedbacks(List<UUID> declaredActivityIds) {
    if (declaredActivityIds.isEmpty()) {
      return List.of();
    }

    return jpaRepository.findDeclaredActivityIdsWithActiveFeedbacks(declaredActivityIds);
  }

  @Override
  public Set<UUID> findAttachmentIdsUsedByTraceSnapshots(
      List<UUID> declaredActivityIds, List<UUID> traceIds) {
    return jpaRepository.findAttachmentIdsUsedByTraceSnapshots(declaredActivityIds, traceIds);
  }

  @Override
  public int countByStatus(Staff staff, Activity activity, EFeedbackStatus status) {
    var spec =
        FeedbackSpecification.hasStaffAuthor(staff.getId())
            .and(FeedbackSpecification.hasActivityId(activity != null ? activity.getId() : null))
            .and(FeedbackSpecification.hasStatus(status));
    return (int) jpaRepository.count(spec);
  }

  // ── private helpers ─────────────────────────────────────────────────

  private Feedback toDomainWithDependencies(FeedbackEntity entity) {
    AssociationsJson associations = entity.getAssociations();

    Map<UUID, User> users = resolveUsers(associations);
    Map<UUID, File> files = resolveFiles(associations);
    Map<UUID, Student> students = resolveStudents(associations);
    Map<UUID, DeclaredSkill> skills = resolveSkills(associations);

    return FeedbackMapper.INSTANCE.toDomain(entity, users, files, students, skills);
  }

  private Map<UUID, User> resolveUsers(AssociationsJson associations) {
    Set<UUID> ids =
        associations.traces().stream()
            .map(AssociationsJson.TraceSnapshot::userId)
            .collect(Collectors.toSet());

    return userJpaRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(AvenirsBaseEntity::getId, UserMapper.INSTANCE::toDomain));
  }

  private Map<UUID, File> resolveFiles(AssociationsJson associations) {
    Set<UUID> ids =
        associations.traces().stream()
            .map(AssociationsJson.TraceSnapshot::attachmentId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    if (ids.isEmpty()) return Map.of();

    return fileJpaRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(AvenirsBaseEntity::getId, FileMapper.INSTANCE::toDomain));
  }

  private Map<UUID, Student> resolveStudents(AssociationsJson associations) {
    Set<UUID> ids =
        associations.declaredSkillProgresses().stream()
            .map(AssociationsJson.DeclaredSkillProgressSnapshot::studentId)
            .collect(Collectors.toSet());

    return studentJpaRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(AvenirsBaseEntity::getId, StudentMapper.INSTANCE::toDomain));
  }

  private Map<UUID, DeclaredSkill> resolveSkills(AssociationsJson associations) {
    Set<UUID> ids =
        associations.declaredSkillProgresses().stream()
            .map(AssociationsJson.DeclaredSkillProgressSnapshot::skillId)
            .collect(Collectors.toSet());

    return declaredSkillJpaRepository.findAllById(ids).stream()
        .collect(
            Collectors.toMap(AvenirsBaseEntity::getId, DeclaredSkillMapper.INSTANCE::toDomain));
  }
}
