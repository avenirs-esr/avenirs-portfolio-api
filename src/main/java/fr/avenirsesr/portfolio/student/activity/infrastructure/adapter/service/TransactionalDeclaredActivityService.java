package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssessmentDetailsData;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityDetailsData;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class TransactionalDeclaredActivityService implements DeclaredActivityService {

  private final DeclaredActivityService delegate;

  @Override
  public PagedResult<DeclaredActivity> getDeclaredActivities(
      PageCriteria pageCriteria,
      String keyword,
      List<EActivityThematic> thematic,
      List<EActivityStatus> status,
      Boolean isValorized) {
    return delegate.getDeclaredActivities(pageCriteria, keyword, thematic, status, isValorized);
  }

  @Override
  public List<DeclaredActivity> getAllDeclaredActivitiesOf(Student student) {
    return delegate.getAllDeclaredActivitiesOf(student);
  }

  @Override
  public PagedResult<DeclaredActivity> getDeclaredActivitiesToAssessBy(
      Staff staff, List<EDeclaredActivityStatus> statuses, PageCriteria pageCriteria) {
    return delegate.getDeclaredActivitiesToAssessBy(staff, statuses, pageCriteria);
  }

  @Override
  public Optional<DeclaredActivity> getByActivity(Activity activity) {
    return delegate.getByActivity(activity);
  }

  @Override
  public DeclaredActivity subscribe(UUID activityId, LocalDate startDate, LocalDate endDate) {
    return delegate.subscribe(activityId, startDate, endDate);
  }

  @Override
  public DeclaredActivity subscribe(
      UUID declaredActivityId, UUID activityId, LocalDate startDate, LocalDate endDate) {
    return delegate.subscribe(declaredActivityId, activityId, startDate, endDate);
  }

  @Override
  public void unsubscribeMultiple(List<UUID> activityIds) {
    delegate.unsubscribeMultiple(activityIds);
  }

  @Override
  public void finish(UUID declaredActivityId) {
    delegate.finish(declaredActivityId);
  }

  @Transactional
  @Override
  public void submitForAssessment(UUID declaredActivityId) {
    delegate.submitForAssessment(declaredActivityId);
  }

  @Override
  public void updateReflection(UUID declaredActivityId, String reflection) {
    delegate.updateReflection(declaredActivityId, reflection);
  }

  @Transactional
  @Override
  public void sendAssessment(
      UUID declaredActivityId, String comment, Integer grade, Boolean validation) {
    delegate.sendAssessment(declaredActivityId, comment, grade, validation);
  }

  @Transactional
  @Override
  public DeclaredActivityDetailsData getDeclaredActivityDetails(UUID declaredActivityId) {
    return delegate.getDeclaredActivityDetails(declaredActivityId);
  }

  @Override
  public DeclaredActivityAssessmentDetailsData getAssessmentDetails(UUID declaredActivityId) {
    return delegate.getAssessmentDetails(declaredActivityId);
  }

  @Override
  public void updateDeclaredActivity(
      UUID declaredActivityId, LocalDate startDate, LocalDate endDate, Boolean valorized) {
    delegate.updateDeclaredActivity(declaredActivityId, startDate, endDate, valorized);
  }

  @Override
  public PagedResult<DeclaredActivity> searchDeclaredActivity(
      String keyword, PageCriteria pageCriteria) {
    return delegate.searchDeclaredActivity(keyword, pageCriteria);
  }

  @Override
  public List<DeclaredActivity> findAllDeclaredActivitiesByIds(List<UUID> ids) {
    return delegate.findAllDeclaredActivitiesByIds(ids);
  }

  @Override
  public List<DeclaredActivity> findAllNotCompletedActivitiesByIds(List<UUID> ids) {
    return delegate.findAllNotCompletedActivitiesByIds(ids);
  }

  @Override
  public boolean areDeclaredActivitiesUnlocked(List<UUID> declaredActivityIds) {
    return delegate.areDeclaredActivitiesUnlocked(declaredActivityIds);
  }

  @Override
  public void checkDeclaredActivitiesUnlocked(List<UUID> declaredActivityIds) {
    delegate.checkDeclaredActivitiesUnlocked(declaredActivityIds);
  }

  @Override
  public Map<DeclaredActivity, EDeclaredActivityStatus> getDeclaredActivityStatus(
      List<DeclaredActivity> declaredActivities) {
    return delegate.getDeclaredActivityStatus(declaredActivities);
  }

  @Override
  public EDeclaredActivityStatus getDeclaredActivityStatus(DeclaredActivity declaredActivity) {
    return delegate.getDeclaredActivityStatus(declaredActivity);
  }

  @Override
  public DeclaredActivity fetchActivityAndCheckLoggedInStudentAuthorization(
      UUID declaredActivityId) {
    return delegate.fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);
  }

  @Override
  public int countEnrolledStudents(Activity activity) {
    return delegate.countEnrolledStudents(activity);
  }

  @Override
  public int countUnsubscriptionsSince(Activity activity, Instant since) {
    return delegate.countUnsubscriptionsSince(activity, since);
  }

  @Override
  public List<DeclaredActivity> getEnrolledStudents(Activity activity) {
    return delegate.getEnrolledStudents(activity);
  }

  @Override
  public List<DeclaredActivity> getEnrolledStudentsNotViewedSince(
      Activity activity, Instant since) {
    return delegate.getEnrolledStudentsNotViewedSince(activity, since);
  }

  @Override
  public List<UUID> getEnrolledStudentIds(Activity activity) {
    return delegate.getEnrolledStudentIds(activity);
  }

  @Override
  public boolean isEnrolled(Activity activity, Student student) {
    return delegate.isEnrolled(activity, student);
  }

  @Transactional
  @Override
  public void delete(UUID declaredActivityId) {
    delegate.delete(declaredActivityId);
  }

  @Transactional
  @Override
  public void deleteAllOfActivity(Activity activity) {
    delegate.deleteAllOfActivity(activity);
  }
}
