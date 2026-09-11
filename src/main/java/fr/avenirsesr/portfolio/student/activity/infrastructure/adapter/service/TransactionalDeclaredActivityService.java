package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssociationsData;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityDetailsData;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionalDeclaredActivityService implements DeclaredActivityService {

  private final DeclaredActivityService delegate;

  @Override
  public PagedResult<DeclaredActivity> getDeclaredActivities(PageCriteria pageCriteria) {
    return delegate.getDeclaredActivities(pageCriteria);
  }

  @Override
  public List<DeclaredActivity> getAllDeclaredActivitiesOf(Student student) {
    return delegate.getAllDeclaredActivitiesOf(student);
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

  @Override
  public void updateReflection(UUID declaredActivityId, String reflection) {
    delegate.updateReflection(declaredActivityId, reflection);
  }

  @Override
  public DeclaredActivityDetailsData getDeclaredActivityDetails(UUID declaredActivityId) {
    return delegate.getDeclaredActivityDetails(declaredActivityId);
  }

  @Override
  public DeclaredActivityAssociationsData getDeclaredActivityAssociations(UUID declaredActivityId) {
    return delegate.getDeclaredActivityAssociations(declaredActivityId);
  }

  @Override
  public void deleteAssociations(UUID declaredActivityId, List<UUID> idsToDelete) {
    delegate.deleteAssociations(declaredActivityId, idsToDelete);
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
  public DeclaredActivityAssociationsData associateActivityWithTraces(
      UUID declaredActivityId, List<UUID> traceIds) {
    return delegate.associateActivityWithTraces(declaredActivityId, traceIds);
  }

  @Override
  public DeclaredActivityAssociationsData associateActivityWithDeclaredSkills(
      UUID declaredActivityId, List<UUID> declaredSkillIds) {
    return delegate.associateActivityWithDeclaredSkills(declaredActivityId, declaredSkillIds);
  }

  @Override
  public PagedResult<AssociationSearchResultData> searchTracesForAssociation(
      UUID declaredActivityId, String keyword, PageCriteria pageCriteria, Boolean isAssociated) {
    return delegate.searchTracesForAssociation(
        declaredActivityId, keyword, pageCriteria, isAssociated);
  }

  @Override
  public PagedResult<AssociationSearchResultData> searchDeclaredActivitiesForAssociation(
      UUID excludeAssociatedWithElementId,
      EAssociationContextType contextType,
      String keyword,
      PageCriteria pageCriteria) {
    return delegate.searchDeclaredActivitiesForAssociation(
        excludeAssociatedWithElementId, contextType, keyword, pageCriteria);
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
  public boolean isEnrolled(Activity activity, Student student) {
    return delegate.isEnrolled(activity, student);
  }

  @Transactional
  @Override
  public void deleteContentActivity(UUID declaredActivityId) {
    delegate.deleteContentActivity(declaredActivityId);
  }
}
